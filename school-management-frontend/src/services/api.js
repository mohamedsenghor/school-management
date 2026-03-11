import axios from 'axios'

// Proxy Vite gère les appels qui ne sortent pas du serveur
const API_BASE_URL = import.meta.env.VITE_API_URL
console.log('API_BASE_URL:', API_BASE_URL)

// Clients SOAP séparés pour chaque endpoint
const classesClient = axios.create({
    baseURL: API_BASE_URL + 'classesWebService',
})

const sectorsClient = axios.create({
    baseURL: API_BASE_URL + 'sectorsWebService',
})

const escapeXml = (value) => {
    if (value === null || value === undefined) {
        return ''
    }

    return String(value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&apos;')
}

// Fonction pour parser la réponse SOAP XML et extraire les données
const parseSOAPResponse = (xmlString) => {
    try {
        const parser = new DOMParser()
        const xmlDoc = parser.parseFromString(xmlString, 'text/xml')

        if (xmlDoc.getElementsByTagName('parsererror').length > 0) {
            throw new Error('Réponse SOAP invalide (XML mal formé).')
        }

        // Chercher les nœuds <return> dans la réponse
        const returns = xmlDoc.getElementsByTagName('return')
        const data = []

        for (let i = 0; i < returns.length; i++) {
            const returnNode = returns[i]

            if (!returnNode.children || returnNode.children.length === 0) {
                const scalarValue = (returnNode.textContent || '').trim()
                if (scalarValue !== '') {
                    if (scalarValue === 'true') {
                        data.push(true)
                    } else if (scalarValue === 'false') {
                        data.push(false)
                    } else {
                        data.push(scalarValue)
                    }
                }
                continue
            }

            const item = {}

            // Extraire tous les éléments enfants et les convertir en objet
            for (let j = 0; j < returnNode.children.length; j++) {
                const child = returnNode.children[j]
                const key = child.tagName
                const value = child.textContent
                item[key] = value || null
            }

            data.push(item)
        }

        return data
    } catch (error) {
        console.error('Erreur lors du parsing SOAP:', error)
        return []
    }
}

const toList = (parsed) => {
    return Array.isArray(parsed) ? parsed : []
}

const toSingle = (parsed) => {
    if (!Array.isArray(parsed) || parsed.length === 0) {
        return null
    }
    return parsed[0]
}

const toBoolean = (parsed) => {
    const first = toSingle(parsed)
    if (typeof first === 'boolean') {
        return first
    }
    if (typeof first === 'string') {
        return first.toLowerCase() === 'true'
    }
    return false
}

// Fonction pour appeler le web service SOAP - Classes
export const classesSOAPRequest = async (soapBody) => {
    try {
        const response = await classesClient.post('', soapBody, {
            headers: {
                'Content-Type': 'text/xml; charset=utf-8',
            }
        })
        // Parser la réponse SOAP et retourner les données
        return parseSOAPResponse(response.data)
    } catch (error) {
        console.error('Erreur lors de l\'appel SOAP (Classes):', error)
        throw error
    }
}

// Fonction pour appeler le web service SOAP - Sectors
export const sectorsSOAPRequest = async (soapBody) => {
    try {
        const response = await sectorsClient.post('', soapBody, {
            headers: {
                'Content-Type': 'text/xml; charset=utf-8',
            }
        })
        // Parser la réponse SOAP et retourner les données
        return parseSOAPResponse(response.data)
    } catch (error) {
        console.error('Erreur lors de l\'appel SOAP (Sectors):', error)
        throw error
    }
}

// Service pour les Classes
export const classesService = {
    getAll: async () => {
        const soapBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sch="http://blackms.dev/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <sch:getAllClasses/>
   </soapenv:Body>
</soapenv:Envelope>`
        return toList(await classesSOAPRequest(soapBody))
    },

    getById: async (classId) => {
        const soapBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sch="http://blackms.dev/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <sch:getClass>
            <classId>${escapeXml(classId)}</classId>
      </sch:getClass>
   </soapenv:Body>
</soapenv:Envelope>`
        return toSingle(await classesSOAPRequest(soapBody))
    },

    save: async (classe) => {
        const soapBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sch="http://blackms.dev/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <sch:saveClass>
            <classe>
                <className>${escapeXml(classe.className)}</className>
                <description>${escapeXml(classe.description || '')}</description>
                <sectorId>${escapeXml(classe.sectorId)}</sectorId>
            </classe>
      </sch:saveClass>
   </soapenv:Body>
</soapenv:Envelope>`
        return toSingle(await classesSOAPRequest(soapBody))
    },

    update: async (classe) => {
        const soapBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sch="http://blackms.dev/school-management">
    <soapenv:Header/>
    <soapenv:Body>
        <sch:updateClass>
            <classe>
                <id>${escapeXml(classe.id)}</id>
                <className>${escapeXml(classe.className)}</className>
                <description>${escapeXml(classe.description || '')}</description>
                <sectorId>${escapeXml(classe.sectorId)}</sectorId>
            </classe>
        </sch:updateClass>
    </soapenv:Body>
</soapenv:Envelope>`
        return toSingle(await classesSOAPRequest(soapBody))
    },

    delete: async (classId) => {
        const soapBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sch="http://blackms.dev/school-management">
    <soapenv:Header/>
    <soapenv:Body>
        <sch:deleteClass>
            <classId>${escapeXml(classId)}</classId>
        </sch:deleteClass>
    </soapenv:Body>
</soapenv:Envelope>`
        return toBoolean(await classesSOAPRequest(soapBody))
    },
}

// Service pour les Secteurs - basé sur le README
export const sectorsService = {
    getAll: async () => {
        const soapBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sch="http://blackms.dev/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <sch:getAllSectors/>
   </soapenv:Body>
</soapenv:Envelope>`
        return toList(await sectorsSOAPRequest(soapBody))
    },

    getById: async (sectorId) => {
        const soapBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sch="http://blackms.dev/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <sch:getSector>
            <sectorId>${escapeXml(sectorId)}</sectorId>
      </sch:getSector>
   </soapenv:Body>
</soapenv:Envelope>`
        return toSingle(await sectorsSOAPRequest(soapBody))
    },

    save: async (sector) => {
        const soapBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sch="http://blackms.dev/school-management">
   <soapenv:Header/>
   <soapenv:Body>
      <sch:saveSector>
         <sector>
                <name>${escapeXml(sector.name)}</name>
         </sector>
      </sch:saveSector>
   </soapenv:Body>
</soapenv:Envelope>`
        return toSingle(await sectorsSOAPRequest(soapBody))
    },

    update: async (sector) => {
        const soapBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sch="http://blackms.dev/school-management">
    <soapenv:Header/>
    <soapenv:Body>
        <sch:updateSector>
            <sector>
                <id>${escapeXml(sector.id)}</id>
                <name>${escapeXml(sector.name)}</name>
            </sector>
        </sch:updateSector>
    </soapenv:Body>
</soapenv:Envelope>`
        return toSingle(await sectorsSOAPRequest(soapBody))
    },

    delete: async (sectorId) => {
        const soapBody = `<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:sch="http://blackms.dev/school-management">
    <soapenv:Header/>
    <soapenv:Body>
        <sch:deleteSector>
            <sectorId>${escapeXml(sectorId)}</sectorId>
        </sch:deleteSector>
    </soapenv:Body>
</soapenv:Envelope>`
        return toBoolean(await sectorsSOAPRequest(soapBody))
    },
}

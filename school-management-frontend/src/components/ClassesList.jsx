import React, { useEffect, useState } from 'react'
import { classesService, sectorsService } from '../services/api'
import '../styles/List.css'

function ClassesList() {
    const [classes, setClasses] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [editingClass, setEditingClass] = useState(null)
    const [deletingClass, setDeletingClass] = useState(null)
    const [creatingClass, setCreatingClass] = useState(false)
    const [sectors, setSectors] = useState([])
    const [formData, setFormData] = useState({ className: '', description: '' })
    const [createFormData, setCreateFormData] = useState({ className: '', description: '', sectorId: '' })

    useEffect(() => {
        fetchClasses()
        fetchSectors()
    }, [])

    const fetchClasses = async () => {
        try {
            setLoading(true)
            const response = await classesService.getAll()
            // La réponse est maintenant un array direct (parsé du SOAP)
            setClasses(Array.isArray(response) ? response : [])
            setError(null)
        } catch (err) {
            setError('Erreur lors du chargement des classes')
            console.error(err)
        } finally {
            setLoading(false)
        }
    }

    const fetchSectors = async () => {
        try {
            const response = await sectorsService.getAll()
            const parsedSectors = Array.isArray(response) ? response : []
            setSectors(parsedSectors)

            if (parsedSectors.length > 0) {
                setCreateFormData((prev) => ({
                    ...prev,
                    sectorId: prev.sectorId || String(parsedSectors[0].id),
                }))
            }
        } catch (err) {
            console.error(err)
        }
    }

    const openCreateModal = () => {
        setError(null)
        setCreatingClass(true)
        setCreateFormData({
            className: '',
            description: '',
            sectorId: sectors.length > 0 ? String(sectors[0].id) : '',
        })
    }

    const closeCreateModal = () => {
        setCreatingClass(false)
        setCreateFormData({ className: '', description: '', sectorId: '' })
    }

    const handleCreateClass = async () => {
        const className = createFormData.className.trim()
        const description = createFormData.description.trim()
        const sectorId = Number(createFormData.sectorId)

        if (!className) {
            setError('Le nom de la classe est obligatoire')
            return
        }

        if (Number.isNaN(sectorId) || sectorId <= 0) {
            setError('Veuillez selectionner un secteur valide')
            return
        }

        try {
            await classesService.save({ className, description, sectorId })
            closeCreateModal()
            await fetchClasses()
        } catch (err) {
            setError('Erreur lors de la creation de la classe')
            console.error(err)
        }
    }

    const openEditModal = (classe) => {
        setError(null)
        setEditingClass(classe)
        setFormData({
            className: classe.className || '',
            description: classe.description || '',
        })
    }

    const closeEditModal = () => {
        setEditingClass(null)
        setFormData({ className: '', description: '' })
    }

    const handleEditClass = async () => {
        if (!editingClass) return

        const className = formData.className.trim()
        const description = formData.description.trim()

        if (!className) {
            setError('Le nom de la classe est obligatoire')
            return
        }

        try {
            await classesService.update({
                id: editingClass.id,
                className,
                description,
                // L'ID secteur existant est conserve, il n'est jamais saisi par l'utilisateur.
                sectorId: editingClass.sectorId,
            })
            closeEditModal()
            await fetchClasses()
        } catch (err) {
            setError('Erreur lors de la mise a jour de la classe')
            console.error(err)
        }
    }

    const openDeleteModal = (classe) => {
        setError(null)
        setDeletingClass(classe)
    }

    const closeDeleteModal = () => {
        setDeletingClass(null)
    }

    const handleDeleteClass = async () => {
        if (!deletingClass) return

        try {
            const deleted = await classesService.delete(deletingClass.id)
            if (!deleted) {
                setError('Suppression refusee par le serveur')
                return
            }
            closeDeleteModal()
            await fetchClasses()
        } catch (err) {
            setError('Erreur lors de la suppression de la classe')
            console.error(err)
        }
    }

    if (loading) return <div className="loading">Chargement des classes...</div>
    if (error) return <div className="error">{error}</div>

    return (
        <div className="list-container">
            <div className="list-header">
                <h2>Classes</h2>
                <div className="header-actions">
                    <button className="btn btn-primary" onClick={openCreateModal}>
                        Ajouter
                    </button>
                    <button className="btn btn-secondary" onClick={fetchClasses}>
                        Rafraichir
                    </button>
                </div>
            </div>

            {classes.length === 0 ? (
                <div className="empty-state">
                    <p>Aucune classe trouvée</p>
                </div>
            ) : (
                <div className="list-grid">
                    {classes.map((classe) => (
                        <div key={classe.id} className="card">
                            <div className="card-header">
                                <h3>{classe.className}</h3>
                            </div>
                            <div className="card-body">
                                <p><strong>ID:</strong> {classe.id}</p>
                                <p><strong>Description:</strong> {classe.description || 'N/A'}</p>
                                <p><strong>Secteur:</strong> {classe.sectorName || 'N/A'}</p>
                            </div>
                            <div className="card-footer">
                                <button className="btn btn-secondary" onClick={() => openEditModal(classe)}>
                                    Editer
                                </button>
                                <button className="btn btn-danger" onClick={() => openDeleteModal(classe)}>
                                    Supprimer
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {editingClass && (
                <div className="modal-overlay" onClick={closeEditModal}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3>Modifier la classe</h3>
                        <div className="modal-field">
                            <label htmlFor="className">Nom</label>
                            <input
                                id="className"
                                type="text"
                                value={formData.className}
                                onChange={(e) => setFormData((prev) => ({ ...prev, className: e.target.value }))}
                            />
                        </div>
                        <div className="modal-field">
                            <label htmlFor="classDescription">Description</label>
                            <textarea
                                id="classDescription"
                                rows="3"
                                value={formData.description}
                                onChange={(e) => setFormData((prev) => ({ ...prev, description: e.target.value }))}
                            />
                        </div>
                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={closeEditModal}>Annuler</button>
                            <button className="btn btn-primary" onClick={handleEditClass}>Enregistrer</button>
                        </div>
                    </div>
                </div>
            )}

            {deletingClass && (
                <div className="modal-overlay" onClick={closeDeleteModal}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3>Confirmer la suppression</h3>
                        <p>Voulez-vous supprimer la classe "{deletingClass.className}" ?</p>
                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={closeDeleteModal}>Annuler</button>
                            <button className="btn btn-danger" onClick={handleDeleteClass}>Supprimer</button>
                        </div>
                    </div>
                </div>
            )}

            {creatingClass && (
                <div className="modal-overlay" onClick={closeCreateModal}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3>Ajouter une classe</h3>
                        <div className="modal-field">
                            <label htmlFor="newClassName">Nom</label>
                            <input
                                id="newClassName"
                                type="text"
                                value={createFormData.className}
                                onChange={(e) => setCreateFormData((prev) => ({ ...prev, className: e.target.value }))}
                            />
                        </div>
                        <div className="modal-field">
                            <label htmlFor="newClassDescription">Description</label>
                            <textarea
                                id="newClassDescription"
                                rows="3"
                                value={createFormData.description}
                                onChange={(e) => setCreateFormData((prev) => ({ ...prev, description: e.target.value }))}
                            />
                        </div>
                        <div className="modal-field">
                            <label htmlFor="newClassSector">Secteur</label>
                            <select
                                id="newClassSector"
                                value={createFormData.sectorId}
                                onChange={(e) => setCreateFormData((prev) => ({ ...prev, sectorId: e.target.value }))}
                            >
                                {sectors.length === 0 ? (
                                    <option value="">Aucun secteur disponible</option>
                                ) : (
                                    sectors.map((sector) => (
                                        <option key={sector.id} value={sector.id}>
                                            {sector.name}
                                        </option>
                                    ))
                                )}
                            </select>
                        </div>
                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={closeCreateModal}>Annuler</button>
                            <button className="btn btn-primary" onClick={handleCreateClass}>Ajouter</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}

export default ClassesList

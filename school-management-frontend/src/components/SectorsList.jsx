import React, { useEffect, useState } from 'react'
import { sectorsService } from '../services/api'
import '../styles/List.css'

function SectorsList() {
    const [sectors, setSectors] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [editingSector, setEditingSector] = useState(null)
    const [deletingSector, setDeletingSector] = useState(null)
    const [sectorName, setSectorName] = useState('')
    const [creatingSector, setCreatingSector] = useState(false)
    const [newSectorName, setNewSectorName] = useState('')

    useEffect(() => {
        fetchSectors()
    }, [])

    const fetchSectors = async () => {
        try {
            setLoading(true)
            const response = await sectorsService.getAll()
            // La réponse est maintenant un array direct (parsé du SOAP)
            setSectors(Array.isArray(response) ? response : [])
            setError(null)
        } catch (err) {
            setError('Erreur lors du chargement des secteurs')
            console.error(err)
        } finally {
            setLoading(false)
        }
    }

    const openCreateModal = () => {
        setError(null)
        setNewSectorName('')
        setCreatingSector(true)
    }

    const closeCreateModal = () => {
        setCreatingSector(false)
        setNewSectorName('')
    }

    const handleCreateSector = async () => {
        const name = newSectorName.trim()
        if (!name) {
            setError('Le nom du secteur est obligatoire')
            return
        }

        try {
            await sectorsService.save({ name })
            closeCreateModal()
            await fetchSectors()
        } catch (err) {
            setError('Erreur lors de la creation du secteur')
            console.error(err)
        }
    }

    const openEditModal = (sector) => {
        setError(null)
        setEditingSector(sector)
        setSectorName(sector.name || '')
    }

    const closeEditModal = () => {
        setEditingSector(null)
        setSectorName('')
    }

    const handleEditSector = async () => {
        if (!editingSector) return

        const name = sectorName.trim()
        if (!name) {
            setError('Le nom du secteur est obligatoire')
            return
        }

        try {
            await sectorsService.update({
                // L'ID secteur est reutilise tel quel, sans saisie utilisateur.
                id: editingSector.id,
                name,
            })
            closeEditModal()
            await fetchSectors()
        } catch (err) {
            setError('Erreur lors de la mise a jour du secteur')
            console.error(err)
        }
    }

    const openDeleteModal = (sector) => {
        setError(null)
        setDeletingSector(sector)
    }

    const closeDeleteModal = () => {
        setDeletingSector(null)
    }

    const handleDeleteSector = async () => {
        if (!deletingSector) return

        try {
            const deleted = await sectorsService.delete(deletingSector.id)
            if (!deleted) {
                setError('Suppression refusee par le serveur')
                return
            }
            closeDeleteModal()
            await fetchSectors()
        } catch (err) {
            setError('Erreur lors de la suppression du secteur')
            console.error(err)
        }
    }

    if (loading) return <div className="loading">Chargement des secteurs...</div>
    if (error) return <div className="error">{error}</div>

    return (
        <div className="list-container">
            <div className="list-header">
                <h2>Secteurs</h2>
                <div className="header-actions">
                    <button className="btn btn-primary" onClick={openCreateModal}>
                        Ajouter
                    </button>
                    <button className="btn btn-secondary" onClick={fetchSectors}>
                        Rafraichir
                    </button>
                </div>
            </div>

            {sectors.length === 0 ? (
                <div className="empty-state">
                    <p>Aucun secteur trouvé</p>
                </div>
            ) : (
                <div className="list-grid">
                    {sectors.map((sector) => (
                        <div key={sector.id} className="card">
                            <div className="card-header">
                                <h3>{sector.name}</h3>
                            </div>
                            <div className="card-body">
                                <p><strong>ID:</strong> {sector.id}</p>
                            </div>
                            <div className="card-footer">
                                <button className="btn btn-secondary" onClick={() => openEditModal(sector)}>
                                    Editer
                                </button>
                                <button className="btn btn-danger" onClick={() => openDeleteModal(sector)}>
                                    Supprimer
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {editingSector && (
                <div className="modal-overlay" onClick={closeEditModal}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3>Modifier le secteur</h3>
                        <div className="modal-field">
                            <label htmlFor="sectorName">Nom</label>
                            <input
                                id="sectorName"
                                type="text"
                                value={sectorName}
                                onChange={(e) => setSectorName(e.target.value)}
                            />
                        </div>
                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={closeEditModal}>Annuler</button>
                            <button className="btn btn-primary" onClick={handleEditSector}>Enregistrer</button>
                        </div>
                    </div>
                </div>
            )}

            {deletingSector && (
                <div className="modal-overlay" onClick={closeDeleteModal}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3>Confirmer la suppression</h3>
                        <p>Voulez-vous supprimer le secteur "{deletingSector.name}" ?</p>
                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={closeDeleteModal}>Annuler</button>
                            <button className="btn btn-danger" onClick={handleDeleteSector}>Supprimer</button>
                        </div>
                    </div>
                </div>
            )}

            {creatingSector && (
                <div className="modal-overlay" onClick={closeCreateModal}>
                    <div className="modal" onClick={(e) => e.stopPropagation()}>
                        <h3>Ajouter un secteur</h3>
                        <div className="modal-field">
                            <label htmlFor="newSectorName">Nom</label>
                            <input
                                id="newSectorName"
                                type="text"
                                value={newSectorName}
                                onChange={(e) => setNewSectorName(e.target.value)}
                            />
                        </div>
                        <div className="modal-actions">
                            <button className="btn btn-secondary" onClick={closeCreateModal}>Annuler</button>
                            <button className="btn btn-primary" onClick={handleCreateSector}>Ajouter</button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    )
}

export default SectorsList

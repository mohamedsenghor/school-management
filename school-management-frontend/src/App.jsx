import { useState } from 'react'
import './App.css'
import ClassesList from './components/ClassesList'
import SectorsList from './components/SectorsList'

function App() {
    const [activeTab, setActiveTab] = useState('classes')

    return (
        <div className="app-container">
            <header className="app-header">
                <h1>Système de Gestion d'École</h1>
                <p>Gestion des Classes et Secteurs</p>
            </header>

            <nav className="nav-tabs">
                <button
                    className={`tab-button ${activeTab === 'classes' ? 'active' : ''}`}
                    onClick={() => setActiveTab('classes')}
                >
                    Classes
                </button>
                <button
                    className={`tab-button ${activeTab === 'sectors' ? 'active' : ''}`}
                    onClick={() => setActiveTab('sectors')}
                >
                    Secteurs
                </button>
            </nav>

            <main className="app-main">
                {activeTab === 'classes' && <ClassesList />}
                {activeTab === 'sectors' && <SectorsList />}
            </main>
        </div>
    )
}

export default App

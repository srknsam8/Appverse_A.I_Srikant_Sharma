import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { Container, Row, Col, Card } from 'react-bootstrap';
import { Sparkles, LayoutGrid, ShieldAlert, Cpu } from 'lucide-react';
import AppDetails from './pages/AppDetails';
import DeveloperConsole from './pages/DeveloperConsole';
import Marketplace from './pages/Marketplace';
import NavigationBar from './components/NavigationBar';
import Login from './pages/Login';
import ProtectedRoute from './components/ProtectedRoute'; 
import AdminDashboard from './pages/AdminDashboard';
import Register from './pages/Register';
import { useNavigate } from 'react-router-dom';

function Home() {
  const navigate = useNavigate();
  
  return (
    <Container className="d-flex flex-column align-items-center justify-content-center pt-5" style={{ minHeight: '85vh' }}>
      <div className="text-center mb-5 animate__animated animate__fadeIn">
        <div className="d-inline-flex align-items-center gap-2 ai-pill-tag mb-3">
          <Sparkles size={14} /> Next-Gen AI Integration Active
        </div>
        <h1 className="display-4 fw-bold" style={{ color: 'var(--text-main)', letterSpacing: '-1px' }}>
          AppVerse <span style={{ color: 'var(--ai-glow)' }}>AI</span>
        </h1>
        <p className="lead text-muted" style={{ maxWidth: '500px' }}>
          An intelligent marketplace dashboard built with React and Spring Boot.
        </p>
      </div>

      <Row className="g-4 w-100" style={{ maxWidth: '900px' }}>
        <Col md={4}>
          {/* --- UPDATED: Clickable Marketplace Card --- */}
          <Card 
            className="app-card-custom p-4 h-100 text-center text-white border-0"
            onClick={() => navigate('/marketplace')}
            style={{ cursor: 'pointer' }}
          >
            <LayoutGrid className="mx-auto mb-3" size={32} style={{ color: 'var(--ai-glow)' }} />
            <h5 className="fw-semibold">Marketplace</h5>
            <p className="small text-muted mb-0">Browse applications organized by smart dynamic tags.</p>
          </Card>
        </Col>

        <Col md={4}>
          <Card className="app-card-custom p-4 h-100 text-center text-white border-0">
            <Cpu className="mx-auto mb-3" size={32} style={{ color: 'var(--ai-glow)' }} />
            <h5 className="fw-semibold">AI Sentiment</h5>
            <p className="small text-muted mb-0">Real-time analytical sentiment monitoring engine.</p>
          </Card>
        </Col>

        <Col md={4}>
          <Card className="app-card-custom p-4 h-100 text-center text-white border-0">
            <ShieldAlert className="mx-auto mb-3" size={32} style={{ color: 'var(--ai-glow)' }} />
            <h5 className="fw-semibold">Role Guard</h5>
            <p className="small text-muted mb-0">Protected console boundaries for Admins & Developers.</p>
          </Card>
        </Col>
      </Row>
    </Container>
  );
}

function App() {
  return (
    <Router>
      <NavigationBar />
      {/* ONE single Routes block to rule them all */}
      <Routes>
        {/* Public Routes (Anyone can see these) */}
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        {/* --- UPDATED: Added Register Route --- */}
        <Route path="/register" element={<Register />} />

        {/* Protected Routes (Must be logged in as ANY role) */}
        <Route 
          path="/marketplace" 
          element={
            <ProtectedRoute>
              <Marketplace />
            </ProtectedRoute>
          } 
        />
        <Route 
          path="/app/:id" 
          element={
            <ProtectedRoute>
              <AppDetails />
            </ProtectedRoute>
          } 
        />

        {/* Role-Protected Routes (Must be a DEVELOPER or ADMIN) */}
        <Route 
          path="/developer-console" 
          element={
            <ProtectedRoute allowedRoles={['DEVELOPER', 'ADMIN']}>
              <DeveloperConsole />
            </ProtectedRoute>
          } 
        />
        {/* Role-Protected Routes (Must be an ADMIN) */}
        <Route 
          path="/admin-dashboard" 
          element={
            <ProtectedRoute allowedRoles={['ADMIN']}>
              <AdminDashboard />
            </ProtectedRoute>
          } 
        />
        
      </Routes>
    </Router>
  );
}

export default App;
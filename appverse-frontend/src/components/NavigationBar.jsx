import React, { useContext } from 'react';
import { Navbar, Nav, Container, Button } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { Sparkles, LogOut, User } from 'lucide-react';

const NavigationBar = () => {
  const { user, logout } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <Navbar expand="lg" variant="dark" style={{ backgroundColor: 'rgba(12, 10, 9, 0.9)', backdropFilter: 'blur(10px)', borderBottom: '1px solid var(--border-color)' }} sticky="top">
      <Container>
        {/* Brand Logo */}
        <Navbar.Brand as={Link} to="/" className="fw-bold d-flex align-items-center gap-2">
          <Sparkles style={{ color: 'var(--ai-glow)' }} size={24} />
          AppVerse <span style={{ color: 'var(--ai-glow)' }}>AI</span>
        </Navbar.Brand>
        
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          
          {/* Main Links */}
          <Nav className="me-auto">
            <Nav.Link as={Link} to="/marketplace" style={{ color: 'var(--ai-glow)' }}>Marketplace</Nav.Link>
            
            {/* Conditional Rendering: Only show if user is a DEVELOPER */}
            {user?.role === 'DEVELOPER' && (
              <Nav.Link as={Link} to="/developer-console" style={{ color: 'var(--ai-glow)' }}>
                Developer Console
              </Nav.Link>
            )}

            {/* Conditional Rendering: Only show if user is an ADMIN */}
            {user?.role === 'ADMIN' && (
              <Nav.Link as={Link} to="/admin-dashboard" className="text-warning fw-bold">
                Admin Dashboard
              </Nav.Link>
            )}
          </Nav>

          {/* User Auth Section */}
          <Nav className="align-items-center">
            {user ? (
              <>
                <Navbar.Text className="me-3 d-flex align-items-center gap-2">
                  <span>
                    <User size={16} className="me-1 mb-1" /> 
                    {user.username || user.email}
                  </span>
                  
                  {/* --- THE PRESENTATION FLEX: Dynamic Role Badge --- */}
                  {user.role && user.role !== 'USER' && (
                    <span className="badge" style={{ backgroundColor: 'var(--ai-glow)', color: '#000', fontSize: '0.65rem' }}>
                      {user.role}
                    </span>
                  )}
                </Navbar.Text>

                <Button variant="outline-danger" size="sm" onClick={handleLogout} className="d-flex align-items-center gap-1">
                  <LogOut size={16} /> Logout
                </Button>
              </>
            ) : (
              <Button as={Link} to="/login" className="px-4 border-0 fw-semibold" style={{ backgroundColor: 'var(--ai-glow)', color: '#000' }}>
                Sign In
              </Button>
            )}
          </Nav>

        </Navbar.Collapse>
      </Container>
    </Navbar>
  );
};

export default NavigationBar;
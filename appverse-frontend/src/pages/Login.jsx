import React, { useState, useContext } from 'react';
import { Container, Card, Form, Button, Alert } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';
import { Lock, Mail } from 'lucide-react';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // Frontend Form Validation
    if (!email || !password) {
      setError('Please fill in all fields.');
      return;
    }

    try {
      setLoading(true);
      
      // Call the global login function and capture the returned user data
      const userData = await login(email, password);
      
      // Safely grab the role (fallback to standard 'USER' just in case)
      const userRole = userData?.role || localStorage.getItem('role') || 'USER';

      // --- THE TRAFFIC COP ROUTING ---
      if (userRole === 'ADMIN') {
        navigate('/admin-dashboard');
      } else if (userRole === 'DEVELOPER') {
        navigate('/developer-console');
      } else {
        // Standard USER
        navigate('/marketplace');
      }

    } catch (err) {
      setError('Invalid credentials or server error. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container className="d-flex align-items-center justify-content-center" style={{ minHeight: '80vh' }}>
      <Card className="app-card-custom p-5 w-100" style={{ maxWidth: '450px' }}>
        <div className="text-center mb-4">
          <h2 className="fw-bold" style={{ color: 'var(--text-main)' }}>Welcome Back</h2>
          <p className="text-muted small">Sign in to access the AppVerse AI console</p>
        </div>

        {error && <Alert variant="danger" className="py-2 text-center">{error}</Alert>}

        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3">
            <Form.Label className="small text-muted fw-semibold d-flex align-items-center gap-1">
              <Mail size={14} /> Email Address
            </Form.Label>
            <Form.Control 
              type="email" 
              placeholder="admin@appverse.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              style={{ backgroundColor: 'rgba(255,255,255,0.05)', color: 'white', border: '1px solid var(--border-color)' }}
            />
          </Form.Group>

          <Form.Group className="mb-4">
            <Form.Label className="small text-muted fw-semibold d-flex align-items-center gap-1">
              <Lock size={14} /> Password
            </Form.Label>
            <Form.Control 
              type="password" 
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              style={{ backgroundColor: 'rgba(255,255,255,0.05)', color: 'white', border: '1px solid var(--border-color)' }}
            />
          </Form.Group>

          <Button 
            type="submit" 
            className="w-100 border-0 fw-bold py-2" 
            style={{ backgroundColor: 'var(--ai-glow)', color: '#000' }}
            disabled={loading}
          >
            {loading ? 'Authenticating...' : 'Sign In'}
          </Button>

          {/* --- SIGN UP LINK --- */}
          <div className="text-center mt-4">
            <span className="text-muted small">Don't have an account? </span>
            <Link to="/register" style={{ color: 'var(--ai-glow)' }} className="text-decoration-none small fw-bold">
              Sign Up Here
            </Link>
          </div>
          
        </Form>
      </Card>
    </Container>
  );
};

export default Login;
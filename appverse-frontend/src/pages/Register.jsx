import React, { useState } from 'react';
import { Container, Card, Form, Button } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { registerUser } from '../services/api';

const Register = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({ username: '', email: '', password: '' });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Send the form data to your Spring Boot backend
      await registerUser({
        username: formData.username,
        email: formData.email,
        password: formData.password
      });
      
      alert("Account created successfully! Please log in."); 
      navigate('/login');
    } catch (error) {
      alert("Registration failed! Check your console or ensure the email isn't already taken.");
      console.error(error);
    }
  };


  return (
    <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '80vh' }}>
      <Card className="bg-dark text-white border-secondary p-5 shadow-lg" style={{ width: '100%', maxWidth: '450px' }}>
        <div className="text-center mb-4">
          <h2 className="fw-bold">Join AppVerse <span style={{ color: 'var(--ai-glow)' }}>AI</span></h2>
          <p className="text-muted">Create a new account to download apps.</p>
        </div>

        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3">
            <Form.Label className="text-muted small">Username</Form.Label>
            <Form.Control 
              type="text" name="username" 
              className="bg-dark text-white border-secondary" 
              onChange={handleChange} required 
            />
          </Form.Group>

          <Form.Group className="mb-3">
            <Form.Label className="text-muted small">Email Address</Form.Label>
            <Form.Control 
              type="email" name="email" 
              className="bg-dark text-white border-secondary" 
              onChange={handleChange} required 
            />
          </Form.Group>

          <Form.Group className="mb-4">
            <Form.Label className="text-muted small">Password</Form.Label>
            <Form.Control 
              type="password" name="password" 
              className="bg-dark text-white border-secondary" 
              onChange={handleChange} required 
            />
          </Form.Group>

          <Button variant="warning" type="submit" className="w-100 fw-bold mb-3">
            Sign Up
          </Button>
          
          <div className="text-center mt-3">
            <span className="text-muted small">Already have an account? </span>
            <Link to="/login" className="text-warning text-decoration-none small fw-bold">Sign In Here</Link>
          </div>
        </Form>
      </Card>
    </Container>
  );
};

export default Register;
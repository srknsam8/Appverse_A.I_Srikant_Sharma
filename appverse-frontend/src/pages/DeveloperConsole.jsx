import React, { useState, useContext } from 'react';
import { Container, Row, Col, Card, Form, Button, Alert } from 'react-bootstrap';
import { AuthContext } from '../context/AuthContext';
import { uploadNewApplication } from '../services/api';

const DeveloperConsole = () => {
  const { user } = useContext(AuthContext); // Get the logged-in user
  
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    releaseNotes: '',
    price: 0,
    version: '1.0.0',
    categoryId: 1 // Defaulting to 1 (Productivity)
  });

  const [status, setStatus] = useState({ type: '', message: '' });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setStatus({ type: '', message: '' });

    try {
      // Pass the form data and the current user's ID to Spring Boot
      await uploadNewApplication(formData, user?.id || 1);
      setStatus({ type: 'success', message: 'Application successfully published to the Marketplace!' });
      
      // Reset form
      setFormData({ title: '', description: '', price: 0, version: '1.0.0', categoryId: 1 });
    } catch (err) {
      setStatus({ type: 'danger', message: 'Failed to upload application. Please check your inputs.' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col md={8}>
          <div className="mb-4">
            <h2 className="fw-bold text-white">Developer Console</h2>
            <p className="text-muted">Publish your AI applications to the global AppVerse marketplace.</p>
          </div>

          {status.message && <Alert variant={status.type}>{status.message}</Alert>}

          <Card className="bg-dark text-white border-secondary p-4">
            <Form onSubmit={handleSubmit}>
              
              <Form.Group className="mb-3">
                <Form.Label className="text-warning fw-bold">Application Title</Form.Label>
                <Form.Control 
                  type="text" 
                  name="title"
                  value={formData.title}
                  onChange={handleChange}
                  placeholder="e.g., Nexus AI Analyzer" 
                  required 
                  className="bg-dark text-white border-secondary"
                />
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label className="text-warning fw-bold">Description</Form.Label>
                <Form.Control 
                  as="textarea" 
                  rows={3} 
                  name="description"
                  value={formData.description}
                  onChange={handleChange}
                  placeholder="What does your application do?" 
                  required 
                  className="bg-dark text-white border-secondary"
                />
              </Form.Group>

              {/* --- NEW: CATEGORY DROPDOWN --- */}
              <Form.Group className="mb-4">
                <Form.Label className="text-warning fw-bold">Application Category</Form.Label>
                <Form.Select 
                  name="categoryId" 
                  value={formData.categoryId}
                  onChange={handleChange} 
                  className="bg-dark text-white border-secondary"
                  required
                >
                  <option value="1">Productivity</option>
                  <option value="2">Games</option>
                  <option value="3">Utilities</option>
                  <option value="4">Education</option>
                </Form.Select>
              </Form.Group>

              <Form.Group className="mb-3">
                <Form.Label className="text-warning fw-bold">Release Notes (What's New?)</Form.Label>
                <Form.Control 
                  as="textarea" 
                  rows={2} 
                  name="releaseNotes"
                  value={formData.releaseNotes}
                  onChange={handleChange}
                  placeholder="e.g., Fixed login bug, added dark mode..." 
                  className="bg-dark text-white border-secondary"
                />
              </Form.Group>

              <Row>
                <Col md={6}>
                  <Form.Group className="mb-4">
                    <Form.Label className="text-warning fw-bold">Price ($)</Form.Label>
                    <Form.Control 
                      type="number" 
                      step="0.01" 
                      min="0"
                      name="price"
                      value={formData.price}
                      onChange={handleChange}
                      required 
                      className="bg-dark text-white border-secondary"
                    />
                  </Form.Group>
                </Col>
                <Col md={6}>
                  <Form.Group className="mb-4">
                    <Form.Label className="text-warning fw-bold">Version</Form.Label>
                    <Form.Control 
                      type="text" 
                      name="version"
                      value={formData.version}
                      onChange={handleChange}
                      required 
                      className="bg-dark text-white border-secondary"
                    />
                  </Form.Group>
                </Col>
              </Row>

              <Button 
                variant="warning" 
                type="submit" 
                className="w-100 fw-bold"
                disabled={loading}
              >
                {loading ? 'Publishing...' : 'Publish to Marketplace'}
              </Button>
            </Form>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default DeveloperConsole;
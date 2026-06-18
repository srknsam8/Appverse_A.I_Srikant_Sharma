import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Badge, Spinner, Alert, Form, InputGroup } from 'react-bootstrap';
import { getAllApplications } from '../services/api';
import { Sparkles, Star } from 'lucide-react';

const Marketplace = () => {
  const [apps, setApps] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // --- FILTER STATE ---
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');

  const navigate = useNavigate();

  // --- NEW: CATEGORY DICTIONARY ---
  // Translates backend numeric IDs into readable words
  const categoryMap = {
    1: 'Productivity',
    2: 'Games',
    3: 'Utilities',
    4: 'Education'
  };

  // Fetch data from Spring Boot when the page loads
  useEffect(() => {
    const fetchApps = async () => {
      try {
        const data = await getAllApplications();
        setApps(data);
      } catch (err) {
        setError('Failed to load the marketplace. Please check your connection.');
      } finally {
        setLoading(false);
      }
    };
    
    fetchApps();
  }, []);

  // --- THE FILTER LOGIC ---
  const filteredApps = apps.filter((app) => {
    const matchesSearch = app.title?.toLowerCase().includes(searchTerm.toLowerCase()) || 
                          app.description?.toLowerCase().includes(searchTerm.toLowerCase());
    
    // Fallback logic: If categoryId is missing from your DTO, this prevents a crash
    const appCategoryId = app.categoryId ? app.categoryId.toString() : 'All';
    const matchesCategory = selectedCategory === 'All' || appCategoryId === selectedCategory;

    return matchesSearch && matchesCategory;
  });

  if (loading) {
    return (
      <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '60vh' }}>
        <Spinner animation="border" variant="warning" />
      </Container>
    );
  }

  return (
    <Container className="py-5">
      <div className="mb-5 animate__animated animate__fadeIn">
        <h2 className="fw-bold text-white">App Marketplace</h2>
        <p className="text-muted">Discover and download the latest AI-driven applications.</p>
      </div>

      {error && <Alert variant="danger">{error}</Alert>}

      {/* --- SEARCH & FILTER UI --- */}
      <Row className="mb-4">
        <Col md={8}>
          <InputGroup>
            <InputGroup.Text className="bg-dark text-warning border-secondary">🔍</InputGroup.Text>
            <Form.Control
              type="text"
              placeholder="Search applications by title or keyword..."
              className="bg-dark text-white border-secondary"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </InputGroup>
        </Col>
        <Col md={4}>
          <Form.Select 
            className="bg-dark text-white border-secondary"
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
          >
            <option value="All">All Categories</option>
            <option value="1">Productivity</option>
            <option value="2">Games</option>
            <option value="3">Utilities</option>
            <option value="4">Education</option>
          </Form.Select>
        </Col>
      </Row>

      <Row className="g-4">
        {filteredApps.length === 0 && !error ? (
          <Col>
            <Alert variant="info" className="bg-dark border-secondary text-white">
              No applications match your search criteria.
            </Alert>
          </Col>
        ) : (
          filteredApps.map((app) => (
            <Col md={4} key={app.id}>
              <Card className="h-100 p-3 bg-dark text-white border-secondary shadow-sm d-flex flex-column">
                <Card.Body className="d-flex flex-column p-0">
                  
                  {/* Title & Version Row */}
                  <div className="d-flex justify-content-between align-items-start mb-2">
                    <div>
                      <Card.Title className="fw-bold text-warning mb-1">{app.title}</Card.Title>
                      
                      {/* UPDATED: Category Badge using the Dictionary */}
                      <span className="badge bg-secondary" style={{ fontSize: '0.75rem' }}>
                        {categoryMap[app.categoryId] || app.category?.name || app.category || 'Uncategorized'}
                      </span>

                    </div>
                    <Badge bg="secondary">v{app.version}</Badge>
                  </div>
                  
                  {/* Average Rating Star (Will show "No reviews" until the backend sends the average math) */}
                  <div className="text-warning small fw-bold mb-3 d-flex align-items-center">
                    <Star size={14} fill="currentColor" className="me-1" />
                    {app.averageRating ? app.averageRating.toFixed(1) : 'No reviews'}
                  </div>

                  {/* Description */}
                  <Card.Text className="text-muted small flex-grow-1" style={{ minHeight: '40px' }}>
                    {app.description}
                  </Card.Text>

                  {/* Footer Row (Price & Button) */}
                  <div className="d-flex justify-content-between align-items-center mt-auto pt-3 border-top border-secondary">
                    <div className="fw-bold text-success">
                      {app.price === 0 ? 'Free' : `$${app.price}`}
                    </div>
                    
                    <button 
                      className="btn btn-outline-warning btn-sm fw-bold"
                      onClick={() => navigate(`/app/${app.id}`)}
                    >
                      View Details
                    </button>
                    
                  </div>
                </Card.Body>
              </Card>
            </Col>
          ))
        )}
      </Row>
    </Container>
  );
};

export default Marketplace;
import React, { useState, useEffect, useContext } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Container, Row, Col, Card, Button, Spinner, Badge, Form, Alert } from 'react-bootstrap';
import { Sparkles, Star } from 'lucide-react';
import { getApplicationById, getReviewsForApp, submitReview, getAllApplications, recordAppDownload } from '../services/api'; 
import { AuthContext } from '../context/AuthContext'; 

const AppDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useContext(AuthContext); 
  
  const [app, setApp] = useState(null);
  const [reviews, setReviews] = useState([]); 
  const [recommendedApps, setRecommendedApps] = useState([]); 
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  // Form State
  const [comment, setComment] = useState('');
  const [rating, setRating] = useState(5);
  const [submitMessage, setSubmitMessage] = useState('');

  // --- NEW: Track the button status! ---
  const [downloadStatus, setDownloadStatus] = useState('idle'); // 'idle', 'downloading', or 'success'

  useEffect(() => {
    const fetchDetails = async () => {
      try {
        const appData = await getApplicationById(id);
        setApp(appData);
        
        const reviewData = await getReviewsForApp(id);
        setReviews(reviewData);

        const allApps = await getAllApplications();
        const suggestions = allApps.filter(a => a.id !== Number(id)).slice(0, 3);
        setRecommendedApps(suggestions);

      } catch (err) {
        setError('Failed to load application details.');
      } finally {
        setLoading(false);
      }
    };

    fetchDetails();
  }, [id]);

  const handleReviewSubmit = async (e) => {
    e.preventDefault();
    
    if (!user) {
      setSubmitMessage('You must be logged in to leave a review.');
      return;
    }

    try {
      const reviewDTO = {
        applicationId: id,
        rating: rating,
        comment: comment
      };
      
      const newReview = await submitReview(reviewDTO, user.id);
      
      setReviews([...reviews, newReview]);
      setComment('');
      setRating(5);
      setSubmitMessage('Review submitted successfully!');
    } catch (err) {
      setSubmitMessage('Failed to submit review. Check console for details.');
    }
  };

  // --- NEW: Smart Download Handler ---
  const handleDownload = async () => {
    try {
      // 1. Change button to loading state
      setDownloadStatus('downloading');
      
      // 2. Record the analytic in the database
      await recordAppDownload(id);
      
      // 3. Fake a 1.5 second download time for realism
      setTimeout(() => {
        setDownloadStatus('success');
        
        // 4. Reset the button back to normal after 3 seconds
        setTimeout(() => {
          setDownloadStatus('idle');
          
          // Optionally refresh app data to show updated count immediately
          setApp(prev => ({...prev, downloadCount: (prev.downloadCount || 0) + 1}));
        }, 3000);
        
      }, 1500);

    } catch (error) {
      console.error("Error clicking download:", error);
      setDownloadStatus('idle'); // Reset if it fails
    }
  };

  const getSentimentBadge = (sentiment) => {
    if (sentiment === 'POSITIVE') return 'success';
    if (sentiment === 'NEGATIVE') return 'danger';
    return 'secondary';
  };

  if (loading) return <Container className="py-5 text-center"><Spinner animation="border" variant="warning" /></Container>;
  if (error) return <Container className="py-5 text-center text-danger"><h4>{error}</h4></Container>;
  if (!app) return null;

  return (
    <Container className="py-5">
      <Button variant="outline-light" className="mb-4" onClick={() => navigate('/marketplace')}>
        &larr; Back to Marketplace
      </Button>

      <Row className="justify-content-center">
        <Col md={10}>
          {/* Main App Details Card */}
          <Card className="bg-dark text-white border-secondary p-5 shadow-lg mb-5">
            <div className="d-flex justify-content-between align-items-start mb-4">
              
              {/* --- UPDATED: Header with Live Metrics --- */}
              <div>
                <h1 className="fw-bold text-warning mb-2">{app.title}</h1>
                <div className="d-flex align-items-center gap-3 mb-3">
                  <Badge bg="secondary" className="fs-6">Version {app.version}</Badge>
                  
                  <span className="text-warning fw-bold d-flex align-items-center">
                    <Star size={16} fill="currentColor" className="me-1" />
                    {app.averageRating ? app.averageRating.toFixed(1) : 'New'}
                  </span>
                  
                  <span className="text-info fw-bold d-flex align-items-center">
                     ⬇ {app.downloadCount ? app.downloadCount.toLocaleString() : '0'} Downloads
                  </span>
                </div>
              </div>

              <h2 className="text-success fw-bold">
                {app.price === 0 ? 'Free' : `$${app.price}`}
              </h2>
            </div>
            
            <hr className="border-secondary mb-4" />
            
            <h5 className="text-muted mb-3">About this Application</h5>
            <p className="fs-5 lh-lg mb-4">{app.description}</p>

            {/* RELEASE NOTES SECTION */}
            {app.releaseNotes && (
              <div className="mb-5 p-3 rounded" style={{ backgroundColor: 'rgba(255, 193, 7, 0.05)', borderLeft: '4px solid #ffc107' }}>
                <h6 className="fw-bold text-warning mb-2">What's New in v{app.version}</h6>
                <p className="text-light small mb-0" style={{ whiteSpace: 'pre-wrap' }}>
                  {app.releaseNotes}
                </p>
              </div>
            )}

            <div className="d-grid gap-2 d-md-flex justify-content-md-end">
              {/* --- NEW: Smart Download Button --- */}
              <Button 
                variant={downloadStatus === 'success' ? 'success' : 'warning'} 
                size="lg" 
                className="fw-bold px-5 d-flex align-items-center justify-content-center gap-2" 
                onClick={handleDownload}
                disabled={downloadStatus !== 'idle'}
              >
                {downloadStatus === 'idle' && (
                  <>Download Application</>
                )}
                
                {downloadStatus === 'downloading' && (
                  <>
                    <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" />
                    Downloading...
                  </>
                )}
                
                {downloadStatus === 'success' && (
                  <>✅ Downloaded!</>
                )}
              </Button>
            </div>
          </Card>

          {/* REVIEWS SECTION */}
          <h3 className="text-warning mb-4">Reviews & AI Sentiment</h3>
          
          {submitMessage && (
            <Alert variant={submitMessage.includes('successfully') ? 'success' : 'danger'}>
              {submitMessage}
            </Alert>
          )}

          <Card className="bg-dark text-white border-secondary p-4 mb-5 shadow">
            <h5 className="mb-3">Leave a Review</h5>
            <Form onSubmit={handleReviewSubmit}>
              <Form.Group className="mb-3">
                <Form.Control 
                  as="textarea" 
                  rows={3} 
                  className="bg-dark text-white border-secondary"
                  placeholder="What do you think of this app? (Gemini AI will analyze your sentiment!)"
                  value={comment}
                  onChange={(e) => setComment(e.target.value)}
                  required
                />
              </Form.Group>
              
              <div className="d-flex justify-content-between align-items-center">
                <Form.Select 
                  className="bg-dark text-white border-secondary w-auto"
                  value={rating}
                  onChange={(e) => setRating(Number(e.target.value))}
                >
                  <option value="5">⭐⭐⭐⭐⭐ (5/5)</option>
                  <option value="4">⭐⭐⭐⭐ (4/5)</option>
                  <option value="3">⭐⭐⭐ (3/5)</option>
                  <option value="2">⭐⭐ (2/5)</option>
                  <option value="1">⭐ (1/5)</option>
                </Form.Select>
                <Button variant="warning" type="submit" disabled={!user}>
                  {user ? 'Post Review' : 'Log in to Post'}
                </Button>
              </div>
            </Form>
          </Card>

          {/* Render Existing Reviews */}
          {reviews.length > 0 ? (
            reviews.map((rev, index) => (
              <Card key={index} className="bg-dark text-white border-secondary mb-3 shadow-sm">
                <Card.Body>
                  <div className="d-flex justify-content-between align-items-center mb-2">
                    <div className="text-warning">
                      {'⭐'.repeat(rev.rating)}{'☆'.repeat(5 - rev.rating)}
                    </div>
                    <Badge bg={getSentimentBadge(rev.sentiment)}>
                      {rev.sentiment ? `AI Sentiment: ${rev.sentiment}` : 'AI Sentiment: PENDING'}
                    </Badge>
                  </div>
                  <Card.Text className="text-light">{rev.comment}</Card.Text>
                </Card.Body>
              </Card>
            ))
          ) : (
            <Alert variant="info" className="bg-dark border-secondary text-white text-center">
              No reviews yet. Be the first to review!
            </Alert>
          )}

          {/* MODULE 3 AI RECOMMENDATIONS */}
          {recommendedApps.length > 0 && (
            <div className="mt-5 pt-4 border-top border-secondary">
              <h4 className="text-warning mb-4 d-flex align-items-center gap-2">
                <Sparkles size={20} /> AI Similar App Recommendations
              </h4>
              <Row className="g-3">
                {recommendedApps.map(rec => (
                  <Col md={4} key={rec.id}>
                    <Card 
                      className="bg-dark text-white border-secondary h-100 shadow-sm"
                      style={{ cursor: 'pointer', transition: '0.3s' }}
                      onMouseEnter={(e) => e.currentTarget.style.borderColor = 'var(--ai-glow)'}
                      onMouseLeave={(e) => e.currentTarget.style.borderColor = '#6c757d'}
                      onClick={() => { navigate(`/app/${rec.id}`); window.scrollTo(0,0); }}
                    >
                      <Card.Body>
                        <h6 className="text-warning fw-bold">{rec.title}</h6>
                        <p className="small text-muted mb-0">
                          {rec.description.length > 60 ? rec.description.substring(0, 60) + '...' : rec.description}
                        </p>
                      </Card.Body>
                    </Card>
                  </Col>
                ))}
              </Row>
            </div>
          )}

        </Col>
      </Row>
    </Container>
  );
};
export default AppDetails;
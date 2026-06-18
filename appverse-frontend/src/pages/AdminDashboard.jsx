import React, { useState, useEffect } from 'react';
import { Container, Row, Col, Card, Table, ProgressBar } from 'react-bootstrap';
import { BarChart3, Users, Download, DollarSign, Activity, ShieldAlert } from 'lucide-react';
import { getAllApplications } from '../services/api'; // --- NEW: Import the API! ---

const AdminDashboard = () => {
  const [loading, setLoading] = useState(true);

  // State to hold the dynamic data
  const [stats, setStats] = useState({
    totalRevenue: 0,
    totalDownloads: 0,
    activeUsers: 0,
    fakeReviewsBlocked: 0
  });

  useEffect(() => {
    const fetchAndCalculateStats = async () => {
      try {
        // 1. Fetch all apps from Spring Boot
        const apps = await getAllApplications();
        
        // 2. Crunch the numbers using Javascript reduce!
        const totalDl = apps.reduce((sum, app) => sum + (app.downloadCount || 0), 0);
        const totalRev = apps.reduce((sum, app) => sum + ((app.price || 0) * (app.downloadCount || 0)), 0);

        // 3. Update the state with the real math
        setStats({
          totalRevenue: totalRev,
          totalDownloads: totalDl,
          activeUsers: 1205, // Mocked for presentation
          fakeReviewsBlocked: 142 // Mocked for presentation
        });
        
      } catch (error) {
        console.error("Failed to fetch analytics data", error);
      } finally {
        setLoading(false);
      }
    };

    fetchAndCalculateStats();
  }, []);

  if (loading) {
    return (
      <Container className="d-flex justify-content-center align-items-center" style={{ minHeight: '60vh' }}>
        <div className="text-warning fs-4 animate__animated animate__pulse animate__infinite">
          Compiling AI Analytics...
        </div>
      </Container>
    );
  }

  return (
    <Container className="py-5 animate__animated animate__fadeIn">
      <div className="mb-4 d-flex align-items-center gap-3">
        <BarChart3 size={32} className="text-warning" />
        <h2 className="fw-bold text-white mb-0">Platform Analytics</h2>
      </div>
      <p className="text-muted mb-5">Real-time revenue, engagement, and AI security metrics.</p>

      {/* --- TOP STAT CARDS --- */}
      <Row className="g-4 mb-5">
        <Col md={3}>
          <Card className="bg-dark border-secondary h-100 shadow-sm">
            <Card.Body className="text-center p-4">
              <DollarSign size={36} className="text-success mb-3" />
              {/* Added toLocaleString logic for perfect currency formatting */}
              <h3 className="text-white fw-bold">
                ${stats.totalRevenue.toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
              </h3>
              <p className="text-muted mb-0">Total Revenue</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="bg-dark border-secondary h-100 shadow-sm">
            <Card.Body className="text-center p-4">
              <Download size={36} className="text-info mb-3" />
              <h3 className="text-white fw-bold">{stats.totalDownloads.toLocaleString()}</h3>
              <p className="text-muted mb-0">App Downloads</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="bg-dark border-secondary h-100 shadow-sm">
            <Card.Body className="text-center p-4">
              <Users size={36} className="text-primary mb-3" />
              <h3 className="text-white fw-bold">{stats.activeUsers.toLocaleString()}</h3>
              <p className="text-muted mb-0">Active Users (7d)</p>
            </Card.Body>
          </Card>
        </Col>
        <Col md={3}>
          <Card className="bg-dark border-secondary h-100 shadow-sm">
            <Card.Body className="text-center p-4">
              <ShieldAlert size={36} className="text-danger mb-3" />
              <h3 className="text-white fw-bold">{stats.fakeReviewsBlocked}</h3>
              <p className="text-muted mb-0">Spam Reviews Blocked</p>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Row className="g-4">
        {/* --- DOWNLOAD TRENDS (Simulated Bar Chart via Progress Bars) --- */}
        <Col md={6}>
          <Card className="bg-dark border-secondary h-100 shadow">
            <Card.Header className="border-secondary bg-transparent pt-3 pb-2">
              <h5 className="text-white d-flex align-items-center gap-2">
                <Activity size={20} className="text-warning"/> Top Performing Categories
              </h5>
            </Card.Header>
            <Card.Body>
              <div className="mb-4">
                <div className="d-flex justify-content-between text-muted small mb-1">
                  <span>Productivity Apps</span>
                  <span>45%</span>
                </div>
                <ProgressBar variant="warning" now={45} className="bg-secondary" style={{ height: '8px' }} />
              </div>
              <div className="mb-4">
                <div className="d-flex justify-content-between text-muted small mb-1">
                  <span>Games</span>
                  <span>30%</span>
                </div>
                <ProgressBar variant="success" now={30} className="bg-secondary" style={{ height: '8px' }} />
              </div>
              <div className="mb-4">
                <div className="d-flex justify-content-between text-muted small mb-1">
                  <span>Utilities</span>
                  <span>15%</span>
                </div>
                <ProgressBar variant="info" now={15} className="bg-secondary" style={{ height: '8px' }} />
              </div>
              <div>
                <div className="d-flex justify-content-between text-muted small mb-1">
                  <span>Education</span>
                  <span>10%</span>
                </div>
                <ProgressBar variant="primary" now={10} className="bg-secondary" style={{ height: '8px' }} />
              </div>
            </Card.Body>
          </Card>
        </Col>

        {/* --- RECENT PLATFORM ACTIVITY TABLE --- */}
        <Col md={6}>
          <Card className="bg-dark border-secondary h-100 shadow">
            <Card.Header className="border-secondary bg-transparent pt-3 pb-2">
              <h5 className="text-white">Recent Security Logs</h5>
            </Card.Header>
            <Card.Body className="p-0">
              <Table variant="dark" hover className="mb-0 border-secondary custom-table">
                <thead>
                  <tr>
                    <th className="text-muted border-secondary pb-2 pt-3 ps-3">Action</th>
                    <th className="text-muted border-secondary pb-2 pt-3">User</th>
                    <th className="text-muted border-secondary pb-2 pt-3">Status</th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td className="ps-3 border-secondary text-light">AI Sentiment Analysis Triggered</td>
                    <td className="border-secondary text-muted">User_992</td>
                    <td className="border-secondary"><span className="badge bg-success">Clean</span></td>
                  </tr>
                  <tr>
                    <td className="ps-3 border-secondary text-light">Developer Upload: CodeFixer Pro</td>
                    <td className="border-secondary text-muted">Dev_Alex</td>
                    <td className="border-secondary"><span className="badge bg-primary">Verified</span></td>
                  </tr>
                  <tr>
                    <td className="ps-3 border-secondary text-light">Bot/Spam Review Detected</td>
                    <td className="border-secondary text-muted">Unknown_IP</td>
                    <td className="border-secondary"><span className="badge bg-danger">Blocked</span></td>
                  </tr>
                  <tr>
                    <td className="ps-3 border-secondary text-light border-0">New User Registration</td>
                    <td className="border-secondary text-muted border-0">Sarah_J</td>
                    <td className="border-secondary border-0"><span className="badge bg-success">Clean</span></td>
                  </tr>
                </tbody>
              </Table>
            </Card.Body>
          </Card>
        </Col>
      </Row>
    </Container>
  );
};

export default AdminDashboard;
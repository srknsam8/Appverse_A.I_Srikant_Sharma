import axios from 'axios';

// Create a custom Axios instance pointing to your Spring Boot server
const api = axios.create({
  baseURL: 'http://localhost:8080',
});

// The "Interceptor": Before any request leaves the frontend, attach the token!
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
  
);

// Fetch all applications for the Marketplace
export const getAllApplications = async () => {
  try {
    const response = await api.get('/api/apps');
    return response.data; // This is the List of DTOs we just made!
  } catch (error) {
    console.error("Error fetching applications:", error);
    throw error;
  }
};
// Upload a new application (Requires Token!)
export const uploadNewApplication = async (appData, developerId) => {
    // 1. Grab the ID Card (JWT Token) from local storage
    const token = localStorage.getItem('token'); 
    
    try {
        // 2. Pass the token in the Authorization header
        const response = await axios.post(
            `http://localhost:8080/api/apps/upload?developerId=${developerId}`, 
            appData,
            {
                headers: {
                    'Authorization': `Bearer ${token}`, // Show the Bouncer the ID!
                    'Content-Type': 'application/json'
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error("Error uploading application:", error);
        throw error;
    }
};
// Fetch a single application by its ID
export const getApplicationById = async (id) => {
  try {
    const response = await api.get(`/api/apps/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching application ${id}:`, error);
    throw error;
  }
};

// Fetch all reviews for a specific application
export const getReviewsForApp = async (applicationId) => {
  try {
    const response = await api.get(`/api/reviews/application/${applicationId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching reviews for app ${applicationId}:`, error);
    return []; // Return empty array if it fails so the UI doesn't crash
  }
};

// Submit a new review
export const submitReview = async (reviewData, customerId) => {
  try {
    // Note: customerId is passed as a query parameter just like your Spring Boot @RequestParam expects!
    const response = await api.post(`/api/reviews/add?customerId=${customerId}`, reviewData);
    return response.data;
  } catch (error) {
    console.error("Error submitting review:", error);
    throw error;
  }
};
// Register a new user
export const registerUser = async (userData) => {
  try {
    // We now have the EXACT path from your Spring Boot controller!
    const response = await api.post('/api/users/register', userData);
    return response.data;
  } catch (error) {
    console.error("Error registering user:", error);
    throw error;
  }
};

export const recordAppDownload = async (appId) => {
    const token = localStorage.getItem('token'); 
    try {
        const response = await axios.post(
            `http://localhost:8080/api/apps/${appId}/download`,
            {}, 
            {
                headers: { 'Authorization': `Bearer ${token}` } 
            }
        );
        return response.data;
    } catch (error) {
        console.error("Failed to record download", error);
    }
};



export default api;
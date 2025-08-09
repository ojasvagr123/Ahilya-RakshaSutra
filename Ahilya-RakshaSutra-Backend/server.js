const express = require('express');
const app = express();
const cors = require('cors');
const dotenv = require('dotenv');
const db = require('./db');
const authRoutes = require('./routes/auth');
const authenticateToken = require('./middleware/authMiddleware');

dotenv.config();
app.use(cors());
app.use(express.json());

// ✅ Public auth routes (register, login)
app.use('/api/auth', authRoutes);

// ✅ Protected route example
app.get('/api/auth/protected', authenticateToken, (req, res) => {
  res.json({
    message: 'Protected data accessed',
    userId: req.user.userId
  });
});

// Root check route
app.get('/', (req, res) => {
  res.send('Ahilya RakshaSutra server is running...');
});

const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
  console.log(`✅ Server started on port ${PORT}`);
});

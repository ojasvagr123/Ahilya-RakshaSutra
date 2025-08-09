const express = require('express');
const bcrypt = require('bcryptjs');
const authMiddleware = require('../middleware/authMiddleware');
const jwt = require('jsonwebtoken');
const pool = require('../db');
require('dotenv').config();

const router = express.Router();

// REGISTER
router.post('/register', async (req, res) => {
    const { name, email, number, address, password } = req.body;
    try {
        const hashedPassword = await bcrypt.hash(password, 10);
        await pool.query(
            'INSERT INTO users (name, email, number, address, password) VALUES ($1, $2, $3, $4, $5)',
            [name, email, number, address, hashedPassword]
        );
        res.json({ message: 'User registered successfully' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// LOGIN (Protected)
router.post('/login',  async (req, res) => {
    const { email, password } = req.body;
    try {
        const result = await pool.query('SELECT * FROM users WHERE email = $1', [email]);
        if (result.rows.length === 0) return res.status(400).json({ error: 'Invalid credentials' });

        const user = result.rows[0];
        const isMatch = await bcrypt.compare(password, user.password);
        if (!isMatch) return res.status(400).json({ error: 'Invalid credentials' });

        const token = jwt.sign({ id: user.id, email: user.email }, process.env.JWT_SECRET, {
            expiresIn: process.env.JWT_EXPIRES_IN
        });

        res.json({ message: 'Login successful', token });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});


// WHATSAPP FORM ROUTE
router.post('/whatsapp-form', authMiddleware, async (req, res) => {
  
  try {
    const { number, message, time, location } = req.body;
    if (!number || !message || !time || !location) {
      return res.status(400).json({ success: false, message: 'All fields are required' });
    }

    const insertResult = await pool.query(
      'INSERT INTO whatsapp_reports (user_id, number, message, time, location) VALUES ($1, $2, $3, $4, $5) RETURNING *',
      [req.user.id, number, message, time, location]
    );

    res.status(201).json({ success: true, data: insertResult.rows[0] });
  } catch (err) {
    console.error("WhatsApp form error:", err.message);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// SMS FORM ROUTE
router.post('/sms-form', authMiddleware, async (req, res) => {
  try {
    const { number, message, time, location } = req.body;

    if (!number || !message || !time || !location) {
      return res.status(400).json({ success: false, message: 'All fields are required' });
    }

    const result = await pool.query(
      'INSERT INTO sms_reports (user_id, number, message, time, location) VALUES ($1, $2, $3, $4, $5) RETURNING *',
      [req.user.id, number, message, time, location]
    );

    res.status(201).json({ success: true, data: result.rows[0] });
  } catch (err) {
    console.error("SMS form error:", err.message);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});

// URL Form Route
router.post('/url-form', authMiddleware, async (req, res) => {
  try {
    const { url, message, time, location } = req.body;

    if (!url || !message || !time || !location) {
      return res.status(400).json({ success: false, message: 'All fields are required' });
    }

    const result = await pool.query(
      'INSERT INTO url_reports (user_id, url, message, time, location) VALUES ($1, $2, $3, $4, $5) RETURNING *',
      [req.user.id, url, message, time, location]
    );

    res.status(201).json({ success: true, data: result.rows[0] });
  } catch (err) {
    console.error("URL form error:", err.message);
    res.status(500).json({ success: false, message: 'Server error' });
  }
});
router.get('/profile', authMiddleware, async (req, res) => {
  try {
    const user = await pool.query(
      'SELECT name, email, number, address FROM users WHERE id = $1',
      [req.user.id]
    );

    // Format times directly in SQL
    const whatsappReports = await pool.query(
      "SELECT number, message, TO_CHAR(time, 'HH24:MI') as time, location FROM whatsapp_reports WHERE user_id = $1",
      [req.user.id]
    );

    const smsReports = await pool.query(
      "SELECT number, message, TO_CHAR(time, 'HH24:MI') as time, location FROM sms_reports WHERE user_id = $1",
      [req.user.id]
    );

    const urlReports = await pool.query(
      "SELECT url, message, TO_CHAR(time, 'HH24:MI') as time, location FROM url_reports WHERE user_id = $1",
      [req.user.id]
    );

    res.json({
      name: user.rows[0]?.name,
      email: user.rows[0]?.email,
      number: user.rows[0]?.number,
      address: user.rows[0]?.address,
      whatsappReports: whatsappReports.rows,
      smsReports: smsReports.rows,
      urlReports: urlReports.rows
    });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Server error' });
  }
});



module.exports = router;

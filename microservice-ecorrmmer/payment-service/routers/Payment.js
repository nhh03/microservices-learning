const express = require('express');
const router = express.Router();    


const { addPayment, updatePaymentStatus, getPaymentByOrderId } = require('../controllers/Payment');

router.get('/:orderId', getPaymentByOrderId);
router.post('/', addPayment);
router.put('/:id', updatePaymentStatus);

module.exports = router;    
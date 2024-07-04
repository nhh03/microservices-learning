const { Kafka } = require('kafkajs')

const kafka = new Kafka({
    clientId: 'pegasus-client',
    brokers: [process.env.KAFKA_BOOTSTRAP_SERVER],
})

module.exports = kafka
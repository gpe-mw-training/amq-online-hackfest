/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.artemis.jms.example;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.qpid.jms.JmsConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AMQPQueueExample {

    private static final Logger LOG = LoggerFactory.getLogger(AMQPQueueExample.class);

    public static void main(String[] args) throws Exception {

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("amq.properties"));
        } catch (IOException ex) {
            LOG.warn("amq.properties not found, defaulting to packaged version.");
            properties.load(AMQPQueueExample.class.getResourceAsStream("/amq.properties"));
        }

        Connection connection1 = null;
        Connection connection2 = null;
        JmsConnectionFactory connectionFactory1 = new JmsConnectionFactory();
        connectionFactory1.setUsername(properties.getProperty("broker.user"));
        connectionFactory1.setPassword(properties.getProperty("broker.passwd"));
        connectionFactory1.setRemoteURI(properties.getProperty("broker.input.uri"));

        JmsConnectionFactory connectionFactory2 = new JmsConnectionFactory();
        connectionFactory2.setUsername(properties.getProperty("broker.user"));
        connectionFactory2.setPassword(properties.getProperty("broker.passwd"));
        connectionFactory2.setRemoteURI(properties.getProperty("broker.results.uri"));
        connectionFactory2.setSendTimeout(Long.parseLong(properties.getProperty("broker.sendTimeout")));

        try {

            // Step 1. Create an amqp qpid 1.0 connection
            connection1 = connectionFactory1.createConnection(properties.getProperty("broker.user"), properties.getProperty("broker.passwd"));
            connection2 = connectionFactory2.createConnection(properties.getProperty("broker.user"), properties.getProperty("broker.passwd"));

            // Step 2. Create a session
            Session session1 = connection1.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Session session2 = connection2.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Step 3. Create a sender
            Queue queue = session1.createQueue(properties.getProperty("broker.input.destination"));

            connection1.start();
            connection2.start();

            MessageConsumer consumer = session1.createConsumer(queue);

            while (true) {

                TextMessage m = (TextMessage) consumer.receive(60000);
                System.out.println("message = " + m.getText());
                
                String[] messages = m.getText().split(System.lineSeparator());

                if (m.getJMSReplyTo() != null) {

                    Queue replyQueue = (Queue) (m.getJMSReplyTo());
                    
                    for (String message : messages) {

                    MessageProducer sender = session2.createProducer((Destination) replyQueue);

                    sender.send(session2.createTextMessage(message));
                    
                    }
                }
            }

        } finally {
            if (connection1 != null) {
                // Step 9. close the connection
                connection1.close();
            }
            if (connection2 != null) {
                // Step 9. close the connection
                connection2.close();
            }
        }
    }
}

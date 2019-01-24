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

        Connection connection = null;
        JmsConnectionFactory connectionFactory = new JmsConnectionFactory();
        connectionFactory.setUsername(properties.getProperty("broker.user"));
        connectionFactory.setPassword(properties.getProperty("broker.passwd"));
        connectionFactory.setRemoteURI(properties.getProperty("broker.uri"));
        connectionFactory.setSendTimeout(Long.parseLong(properties.getProperty("broker.sendTimeout")));

        try {

            // Step 1. Create an amqp qpid 1.0 connection
            connection = connectionFactory.createConnection(properties.getProperty("broker.user"), properties.getProperty("broker.passwd"));

            // Step 2. Create a session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Step 3. Create a sender
            Queue queue = session.createQueue(properties.getProperty("broker.destination"));

            connection.start();

            // Step 5. create a moving receiver, this means the message will be removed from the queue
            MessageConsumer consumer = session.createConsumer(queue);

            while (true) {
                TextMessage m = (TextMessage) consumer.receive(60000);
                System.out.println("message = " + m.getText());
            }

        } finally {
            if (connection != null) {
                // Step 9. close the connection
                connection.close();
            }
        }
    }
}

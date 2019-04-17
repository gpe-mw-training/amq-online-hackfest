# AMQ Online Hackfest

This repository contains resources used in the AMQ Online Hackfest. 

## Scenarios

The Hackfest is prepared with 2 scenarios that highlight important use cases for AMQ Online:

* [Scenario 1 - Hackfest Services](scenario1.md)
* [Scenario 2 - FestHack Technologies](scenario2.md)

## Installing AMQ Online

For this hackfest, we will be using the latest upstream (EnMasse) version: 0.28.0 . You can get it
[here](https://github.com/EnMasseProject/enmasse/releases).

Have a look at the [documentation](https://enmasse.io/documentation/0.28.0/openshift) for installing
and using.

### Installing plans

We provide example configs and plans for AMQ Online to be used as a starting point for the
scenarios. These can be found in the `plans` folder. Once AMQ Online is
installed, they can be applied by running:

```
oc apply -f plans -n enmasse-infra
```

## Example clients

Some example clients in Java can be found [here](https://github.com/EnMasseProject/enmasse-example-clients).

Any client supporting the standard protocols used by AMQ Online (AMQP, MQTT etc), so these clients should
only be considered an example of the different ways you can connect to the address spaces you have
created.

There are currently 3 Java-based clients:

* vertx-example-client - Vert.X based client configured using a properties file, shows how to access EnMasse externally
* quarkus-example-client - Vert.X based client running on-cluster, compiled using quarkus
* jms-example-client - JMS-based configured to read AddressSpace info and use service account for authentication.

Both examples come with resources that you deploy to provision messaging.

# Uninstalling 

```
oc delete rolebindings -l app=enmasse -n kube-system
oc delete clusterrolebindings -l app=enmasse
oc delete crds -l app=enmasse
oc delete clusterroles -l app=enmasse
oc delete apiservices -l app=enmasse
oc delete oauthclients -l app=enmasse
oc delete project enmasse-infra
```

# Provisioning server #

## Context ##
Provisioning, in the context of VoIP and other telecommunication, means providing an automated process to make a 
device able to connect and configure itself to be enabled to make and receive calls. This normally happens when the 
device is plugged and boots up, connecting to a central server that releases the needed configuration.
The scope of this application is to create a simple provisioning server that is able to generate dynamically the 
needed configuration for every device type. 

## Requirements ##
There are two device types in the system:

- Desk: used on office desks
- Conference: used in conference rooms

When users connect these devices to the network, they should automatically get their configuration from server and 
be enabled to make and receive phone calls. They are different physical devices thus they are using different configuration files.
What they have in common is that, when booting, they perform the following HTTP request to the server, putting their MAC 
address in the URL:

```
GET /api/v1/provisioning/aa-bb-cc-11-22-33
```

The server stores a table that contains all the phones in the inventory. If a phone is found in the inventory then its
configuration file should be dynamically generated, according to the phone model configuration format. If a phone is not 
found in the inventory, the server should deny the provisioning request, returning a proper HTTP error code. 

### Configuration file formats ###
The two device types use the following configuration file formats:

#### Desk: Property file ####

```
username=jon
password=doe
domain=sip.voverc.com
port=5060
codecs=G711,G729,OPUS
```

#### Conference: JSON file ####
  
```json
{
  "username" : "john",
  "password" : "doe",
  "domain" : "sip.voverc.com",
  "port" : "5060",
  "codecs" : ["G711","G729","OPUS"]
}
```

The final configuration file should be created by taking data from database and from configuration properties contained 
in `provisioning.*` namespace in `application.properties`

### Override fragment ###
In addition to automatic provisioning described above, there should be the possibility to manually override final 
configuration file, by providing a file fragment (Property or JSON file) that can replace or add some configuration 
properties. Let's see the two cases:

#### Desk: Property file ####
```
username=john
password=doe
domain=sip.anotherdomain.com
port=5161
codecs=G711,G729,OPUS
timeout=10
```
where the override fragment is:
```

domain=sip.anotherdomain.com
port=5161
timeout=10
```

#### Conference: JSON file ####
  
```json
{
  "username" : "jon",
  "password" : "doe",
  "domain" : "sip.anotherdomain.com",
  "port" : "5161",
  "codecs" : ["G711","G729","OPUS"],
  "timeout" : 10
}
```
where the override fragment is:
```

{
  "domain" : "sip.anotherdomain.com",
  "port" : "5161",
  "timeout" : 10 
}
``` 

## How to access database ###
Database is automatically recreated at startup with sample data. You can connect to [H2 Console](http://localhost:8080/h2-console), using the following parameters:

- JDBC URL: `jdbc:h2:mem:test`
- User Name: `sa`
- Password: `password`
 
## Project delivery
The final output of the project should include the following artifacts:

- Complete `ProvisioningController` to handle provisioning requests according to the given request format
- Complete `ProvisioningServiceImpl` implementation in order to support provisioning requests. Create all the necessary 
classes to realize the best possible implementation, considering OOP principles   
- Make `ProvisioningServiceImpl` able to support override fragments for some devices, according to the requirements
- Tests for the implemented classes

All the code should be written in __**your own fork**__ of the repository (you can't push branches on this repository). 
These are the steps:

1. You create a public fork of this project on your Bitbucket account clicking [here](https://bitbucket.org/voverc/provisioning-hw/fork)
2. You create a dedicated branch and a dedicated Pull Request towards your public fork (not towards https://bitbucket.org/voverc/provisioning-hw please)
3. You share the Pull Request link with the reviewer

Note: The system will be tested by the reviewer on the sample data by running `results.sh`
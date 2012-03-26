# Satellite Tracker

A small and simple satellite tracking application.
This application has been created as a tutorial to demonstrate how spring 3, jpa and vaadin can be combined to quickly 
develop web-applications. The project is derived from SpringApplication in the vaadin incubator.

## Data

Currently, only a subset of available satellite data has been extracted from [space-track.org](http://www.space-track.org).
When the application is started, the latest TLE data is retrieved from [celestrak.com](http://www.celestrak.com).

## Usage

    mvn clean package
    mvn jetty:run
    
    firefox http://localhost:8080/sat-tracker

## Hacks

In order to get the application working in eclipse with the GWT development mode, some hacks had to be added:

* src/main/webapp/WEB-INF/jetty-web.xml: promote slf4j to system classes
* src/main/resources/META-INF/spring.handlers + spring.schemas: copy these files from spring-tx.jar
* lib: added custom packaged orekit-6.0-SNAPSHOT-tn.jar with some bugfixes

## Links

* [Vaadin](http://www.vaadin.org)
* [Spring](http://www.springsource.org)
* [Orekit](http://www.orekit.org)
* [Vaadin Spring Integration](http://dev.vaadin.com/browser/incubator/SpringApplication)

## License

This software is released under the Apache License 2.0

   Copyright 2012 Thomas Neidhart

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
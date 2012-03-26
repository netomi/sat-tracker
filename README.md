# Satellite Tracker

A small and simple satellite tracking application.
This application has been created as a tutorial to demonstrate how spring 3, jpa and vaadin can be combined to quickly 
develop web-applications.

## Data

Currently, only a subset of available satellite data has been extracted from http://www.space-track.org.
When the application is started, the latest TLE data is retrieved from http://www.celestrak.com.

## Usage

    mvn clean package
    mvn jetty:run
    
    firefox http://localhost:8080/sat-tracker

## Links

* [Vaadin](http://www.vaadin.org)
* [Spring](http://www.springsource.org)
* [Orekit](http://www.orekit.org)

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
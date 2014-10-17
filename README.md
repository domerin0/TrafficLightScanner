TrafficLightScanner
===================
--IN PROGRESS-- NOT CURRENTLY WORKING

Uses opencv to scan for traffic lights using camera. The intent is to generalize this to be workable for any traffic light
in any jurisdiction. This is more of a proof-of-concept to see if it is indeed possible to read the state of a traffic light 
(while in motion or stopped) with only a smartphone. The main idea is to look for certain colours {red, green, yellow}, and then for circles
that are within an acceptable range to be a traffic light.

Potential problems:
May pick up break lights of vehicles.
May pick up other coloured circles on advertisements or signs or other sources of stimulus.

Proposed Solution (tentative):
Define a region which is where the traffic light should be, (based of both previous data and some predefined parameters)
Exclude scanning in other regions.
Scans traffic lights and gets state.

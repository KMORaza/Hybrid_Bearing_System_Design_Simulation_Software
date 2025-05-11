## Software for Hybrid Bearing System Design Simulation
This desktop app provides a comprehensive simulation environment for analyzing hybrid bearing systems, combining magnetic, ceramic, and hybrid bearing technologies.

* Physics model :—
  * Implements a Runge-Kutta 4th order integration for accurate dynamics simulation.
  * Includes thermal modeling with heat generation/dissipation calculations.
  * Models magnetic field effects for magnetic/hybrid bearings.
  * Calculates stress distribution based on material properties.
  * Implements contact mechanics with friction modeling.
  * Rotor motion: Simulates 1D vertical displacement with stiffness (spring-like restoring force), damping (velocity-dependent resistance), gyroscopic effects (speed-dependent coupling).
  * Contact Mechanics (Hertzian contact)
    * Stress ∝ Load / (Material Stiffness × Contact Area)
    * Friction ∝ Load × Speed (empirical model)
  * Thermal system
    * Frictional losses
    * Eddy currents (in magnetic bearings)
    * Convection (surface cooling)
    * Conduction (through materials)
* Control system :—
  * PID controller with anti-windup protection.
  * Kalman filter for state estimation.
  * Proper clamping of all values to prevent numerical instability.
  * Real-time tuning through interactive sliders.
 
| ![](https://github.com/KMORaza/Hybrid_Bearing_System_Design_Simulation_Software/blob/main/src/main/screenshots/001.png) | ![](https://github.com/KMORaza/Hybrid_Bearing_System_Design_Simulation_Software/blob/main/src/main/screenshots/002.png) |
|-------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| ![](https://github.com/KMORaza/Hybrid_Bearing_System_Design_Simulation_Software/blob/main/src/main/screenshots/003.png) |  |
| ![](https://github.com/KMORaza/Hybrid_Bearing_System_Design_Simulation_Software/blob/main/src/main/screenshots/005.png) | ![](https://github.com/KMORaza/Hybrid_Bearing_System_Design_Simulation_Software/blob/main/src/main/screenshots/006.png) |

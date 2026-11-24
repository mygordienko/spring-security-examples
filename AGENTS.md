# Spring Security Examples

This is an education project, its goal is to demonstrate typical usage scenarios for Spring Security.
This is a multi-module project, each module represents a specific security-related problem and how it 
is solved with Spring Security.

All modules are implemented with Java, Spring Boot and Thymeleaf for UI forms.
Gradle is used as a build system

# Implementation/Coding guidelines

* Each scenario must have its own module (with submodules if necessary).
* Do not extend existing modules with new cases, create new modules do demonstrate new cases/problems.
* Avoid bringing in external systems if sufficient analog can be implemented using Spring, ex. if OAuth2 
  server is needed for the case, use Spring Authorization Server, do not bring Keycloak or similar systems.
* Summary for each module is shown in `ReadMe.md` and each module has its own `ReadMe.md` with details.

# Development Workflow Delegation

For non-trivial tasks, act as an orchestrator rather than doing everything in the main session, 
use the following workflow with subagents:

* First use scout to survey the codebase and identify relevant files, entry points, and risks.
* Then ask oracle to review the scout's findings and challenge assumptions.
* Have planner produce a specific implementation plan based on the above.
* Then have worker implement according to the plan.
* Finally, use reviewer for a read-only review, then report back to the user.
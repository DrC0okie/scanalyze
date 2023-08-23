# Development Methodology Document



**Authors**: Anthony David, Jarod Streckeisen, Timothée Van Hove



## Introduction

The purpose of this Development Methodology Document is to outline a structured approach for the successful delivery of our 3-week Scanalyze project. Given the project's tight schedule, complex deliverables, and the need for cross-functional collaboration among team members, it's imperative to adopt an organized, transparent, and efficient development methodology. This document aims to serve as a comprehensive guide that details the set of methodologies, tools, and best practices the team has agreed upon. This includes an explanation of the Agile and Scrum frameworks we will employ, the Git workflow strategies for source code management, and our approaches for task decomposition and work distribution. Ultimately, this document seeks to serve as a foundational blueprint to align team members and set the stage for effective communication, rigorous quality checks, and timely delivery of project milestones.



## Objectives and Scope

The primary objective of the Scanalyze project is to revolutionize personal expense management in the realm of grocery shopping. Utilizing Optical Character Recognition (OCR) technology, the Android application aims to automate the process of data extraction from physical grocery receipts, thus eliminating the need for manual entry and facilitating efficient expense tracking.

### Core Objectives:

1. **User-Friendly Interface**: To develop an intuitive mobile interface that allows users to easily scan their grocery receipts.
2. **Data Automation**: To implement OCR capabilities for automated data extraction from scanned receipts.
3. **Data Analytics and Visualization**: To provide robust analytics features that offer users insights into their spending habits through numerical and graphical summaries.
4. **Secure Authentication and Data Storage**: To ensure the secure handling of user information through a reliable authentication system and protected data storage.



## Development Methodologies

### Overview

To accommodate our tight 3-week schedule and ensure a high-quality end product, we are adopting a blend of Agile-Scrum and Extreme Programming  (XP) methodologies. Our sprints will span one week each, enabling rapid cycles of development, testing, and feedback.

### Team Composition

**Anthony David**: UX/UI Designer, Database Architect and Product Owner

**Responsibilities**: Lead on website design, mockups, mobile app design, database creation, product logo, and color themes. As the Product Owner, Anthony will also prioritize the backlog and interact with stakeholders.

**Jarod Streckeisen**: Full-stack Web Developer, Technical lead

**Responsibilities**: In charge of the landing page, API server, product-matching algorithms, and potential help with the database. Jarod will be responsible of the CI/CD pipeline of the API web server. Also responsible for API testing and performance testing.

**Timothée Van Hove**: Mobile Application Developer, Scrum Master

**Responsibilities**: Responsible for mobile application development and automated unit and integration tests as a part of the development process. As Scrum Master, Timothée will facilitate Scrum ceremonies and remove obstacles that might impede development progress. Timothée will be responsible of the CI/CD pipeline of the mobile application. 

### Sprint Plan

#### Sprint 1 (Week 1)

**Deliverables and role attribution**:

1. Development Methodology Document (Timothée)
2. Requirement Specification Document (Timothée)
3. Landing Page (Jarod)
4. Mockups for the Android application (Anthony)
5. CI/CD Pipelines (Timothée & Jarod)
6. 5-minute pitch preparation (Anthony)

#### Sprint 2 (Week 2)

**Deliverables**: To be determined based on the outcome of Sprint 1

#### Sprint 3 (Week 3)

**Deliverables**:

1. Finalized Android application with API Backend
2. 15-minute presentation including a video demo

### Development Workflow and Tools

#### Git branching strategy and conventions

We will use a feature-branch workflow. Each new feature or bug fix will be developed in a separate branch. Each feature branch must be merge into the develop branch. The main branch will remain deployable at all times.

**branches**: Every feature branch must begin with `ft` followed by the name of the feature. words are separated by hyphens. Example : `ft-my-feature`

#### CI/CD Pipeline

As suggested, we will implement a CI/CD pipeline using GitHub Actions. The pipeline will automate testing and, once we're ready to release a version, deployment.



### Task Decomposition and Allocation

Given the tight schedule, we recognize the potential for risks such as scope creep, development bottlenecks, and unforeseen technical challenges. We'll have a brief daily stand-up to discuss what each member is working on, any roadblocks, and how they can be resolved quickly.



### Agile Methodology

In navigating the tight deadlines and evolving requirements of the Scanalyze project, we have opted for the Agile methodology. This approach encourages iterative development, rapid feedback loops, and stakeholder engagement—all of which are vital given our three-week timeframe and yet-to-be-finalized second sprint. With its focus on incremental deliverables and stakeholder feedback, Agile enables us to manage risks proactively, ensuring that the final product will align with both user needs and academic expectations.



### XP (Extreme Programming) Practices

While Scrum provides us with a framework for project management, Extreme Programming (XP) offers specific technical practices designed to improve software quality and responsiveness to changing requirements. For the Scanalyze project, we've chosen to integrate the following XP practices:

1. **Pair Programming**: Given our team's diverse strengths, pair programming will be instrumental. For instance, when working on the mobile app's integration with the API server, Timothée could pair with Jarod, leveraging Jarod's expertise in full-stack development. This not only ensures high-quality code but also facilitates knowledge transfer within the team.
2. **Test-Driven Development (TDD)**: Quality assurance is paramount. By writing tests before the actual code, TDD ensures that our application meets its requirements from the outset. For example, as Timothée develops the scanning algorithms, tests can be written first to define the expected behavior. 
3. **Continuous Integration and Continuous Deployment (CI/CD)**: Given our tight schedule, integrating changes frequently and ensuring they work seamlessly is vital. Our CI/CD pipelines, implemented using GitHub Actions, will automatically test and deploy our code, ensuring consistent product quality and rapid release cycles.
4. **Collective Code Ownership**: To promote flexibility within our team, every team member should feel comfortable working on and improving any part of the codebase. This approach ensures that we're not bottlenecked if a member is occupied with another task and fosters a collaborative code culture.

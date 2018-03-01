# abcBanking
API to provide managing token queues in a Banking system

API documentation available at https://prabalghura.github.io/abcBanking/javadoc

Test coverage report available at https://prabalghura.github.io/abcBanking/cobertura/index.html

Below is the DB Model for the solution:

![abcbanking model](https://user-images.githubusercontent.com/34856263/36524251-5dbc46d2-17cb-11e8-96f8-2d70824c1985.png)

Entities in the system:

1. **User**: This represents a Bank employee. Users are assigned to branches or counters, or a user can have admin role to perform admin tasks. Only a user with role ADD_NEW_USER can add new Users

2. **Role**: This is a weak entity representing an authorization given to a User. It is just for storing admin task related roles. This entity can only updated from Backend whenever there's a need to define new admin related roles.

3. **Branch**: System supports multiple bank branches. There's a manager assigned to each branch. Only a user with role ADD_NEW_BRANCH can add or modify a branch (reassign manager).

4. **Counter**: Branch can have multiple counters. A counter has a User assigned to it as operator. A counter has a servicing type (REGULAR/PREMIUM) which represents the type of customers it serves. A counter also has a branch specific Id/number assigned to it. Only a Branch manager can add or update (assign new operator) counters in his branch. A counter has a list of tokens it needs to serve. It also has list of service steps it can serve.

5. **Services**: It represents various services which Bank offer, not all branches serve all the services. A service can have 1 or more steps that need to be performed sequentially to complete a service. A user with role DEFINE_SERVICE can add new services.

6. **Service Step**: It represents an undivisible step that constitutes a part of service (like deposit, enquiry, etc.). A user with role DEFINE_SERVICE can add new service steps.

7. **Customer**: It represents a customer with unique account number in the system. Customer can request a token at a branch. Customer has type REGULAR/PREMIUM

8. **Token**: It is a request for service(s) with a workflow of steps that needed to be served sequentially.

Relationship among entities:

1. **User-Role**: It is a many to many mapping stored in relationship USERXROLE. Only a user with role ASSIGN_ROLES can assign roles to other users.

2. **Service-Service Step**: It is a many to many mapping stored in relationship SERVICEXSERVICE_STEP. There's also an order which needs to be followed while performing service for execution of steps. A user with role DEFINE_SERVICE can update this mapping.

3. **Counter-Service Step**: It is a many to many mapping stored in relationship COUNTERXSERVICE_STEP. A Branch manager can define which counters in his branch can serve which service steps. This relationship alongwith Service-Service Step mapping can effectively map which branch can serve which services which can be displayed to customer while customer is requesting a token.

4. **Token-Service Step**: It is a many to many mapping stored in relationship TOKEN_WORKFLOW. When a user requests for a list of services, the services are broken down into steps sequentially (from Service-Service Step mapping) that are needed to be followed to complete the token service. All the steps are initially marked Pending. The first step is assigned to a counter **best counter within branch** and changed from Pending to Assigned. This marking gives rise to Counter-Token Relationship.

5. **Counter-Token Relationship**: Token-Service Step workflow when assigned to a counter gives that counter a list of tokens that are needed to be served, these are internally maintained as queue (EXPLAINED SEPARATELY). Whenever a counter serves a token, that step is marked as COMPLETED, current operator of counter is also registered, comments (if any) are registered. Next step (if any) is marked as ASSIGNED and assigned to **best counter within branch**.

4, 5 together forms a Counter-Token-Service Step which will be called **Token Workflow** here onwards. Token Workflow is also related to User for tracking which operator served that step for a token currently being operator of a counter, so that counter operators can be changed afterwards without disturbing track of workflow.

**Best counter within branch**: A Token is held by customer having a type, all the counters within a branch serving that customer type are listed. Counters which are not serving current token_workflow step are removed from the list. From the remaining list best counter is the one having minimum current token queue size.

## Caching strategy
For faster access, application maintains an internal JVM cache with following structure:

ConcurrentHashMap of Branches
<ul> 
  <li> List of Regular Services
    <ul>
      <li> List of service steps </li>
    </ul>
  </li>
  <li> List of Premium Services
    <ul>
      <li> List of service steps </li>
    </ul>
  </li>
  <li> Synchronized Token number generator </li>
  <li> ConcurrentHashMap of Regular Counters
    <ul>
      <li> List of service steps served </li>
      <li> ConcurrentLinkedQueue of Tokens
        <ul>
          <li> Type of token mapped from Customer type it serves</li>
          <li> List of TokenWorkflow steps </li>
        </ul>
      </li>
    </ul>
  </li>
  <li> ConcurrentHashMap of Premium Counters
    <ul>
      <li> List of service steps served </li>
      <li> ConcurrentLinkedQueue of Tokens
        <ul>
          <li> Type of token mapped from Customer type it serves</li>
          <li> List of TokenWorkflow steps </li>
        </ul>
      </li>
    </ul>
  </li>
</ul>

1. When the application starts, this cache is fetched from DB in breadth-first-search manner. This is a heavy & recursive DB operation but it is never repeated except in one case when there is a change in service-service step mapping (This is an admin task and in an ideal scenario very rare).
2. If a new branch is added or updated in the system. Its instance is updated in Branch cache.
3. If a new counter is added or updated (assigning operator or steps) in the branch. Its instance within branch instance is updated (again a concurrent map inside branch), branch cache is also updated.
4. When a token is created, it is populated with pending workflow steps from list of services requested. First step is marked Assigned and token is assigned to best counter in branch.
5. When a new token is assigned to a counter, it is added to the tail of counter's ConcurrentLinkedQueue of Tokens
6. When a counter serves a token it is removed from head of counter's ConcurrentLinkedQueue of Tokens. Tokens workflow steps (minimum 1 maximum 2) are updated and token is assigned to next counter, if required.
7. When a token is marked cancelled/completed within a branch (only branch manager or current operator of counter to which token is assigned can do this). All the counter queues within a branch are looped for searching the specified token. If found, token is removed from counter queue.

4, 6, 7 are high-frequency operations requiring DB update.

Rest all the operations are very straightforward, I have not included them here for brevity of this document. Please refer to source/documentation for them.

<h3>Model JSON structure</h3>
<ol>
<li>Role: {<br>
  "name": String, <br>
}</li>

<li>User: {<br>
  "userId": String, <br>
  "name": String <br>
}</li>

<li>Customer: {<br>
  "accountNumber": long, <br>
  "name": String, <br>
  "phoneNumber": String, (optional) <br>
  "address": String, (optional) <br>
  "type": String (values=[REGULAR, PREMIUM]) <br>
}</li>

<li>ServiceStep: {<br>
  "id": long, <br>
  "name": String <br>
}</li>

<li>Service: {<br>
  "id": long, <br>
  "name": String, <br>
  "steps": [ServiceStep] <br>
}</li>

<li>TokenWorkflow: {<br>
  "stepId": long, <br>
  "servedBy": String, <br>
  "status": String (values=[COMPLETED,	ASSIGNED,	PENDING]) <br>
  "comments": String <br>
}</li>

<li>Token: {<br>
  "accountNumber": long, <br>
  "number": Integer, <br>
  "steps": [TokenWorkflow] <br>
}</li>

<li>Counter: {<br>
  "currentOperator": String, <br>
  "number": Integer, <br>
  "servicingType": String, (values=[REGULAR, PREMIUM]) <br>
  "steps": [ServiceStep], <br>
  "tokens": [Token] <br>
}</li>

<li>Branch: {<br>
  "id": long, <br>
  "name": String, <br>
  "managerId": String, <br>
  "regularServices": [Service], <br>
  "premiumServices": [Service], <br>
  "counters": [Counter] <br>
}</li>
</ol>

<h3>Rest API's :</h3>
baseUrl: /api <br>
<ol>

<li>URL: /branches/{branchId} <br>
Method : GET <br>
Response : Branch<br>

For getting a specific branch.
</li>

<li>URL: /branches/{branchId}/counters/{counterId}/service <br>
Method : POST <br>
RequestHeader: {"userId": String}<br>
Response : {SUCCESS: String} <br>

To service first token in the counter queue
</li>

<li>URL: /branches/{branchId}/customer/{accountNumber}/token <br>
Method : POST <br>
RequestBody: [Services] <br>
Response : Token <br>

For creating token for an existing customer
</li>

<li>URL: /branches/{branchId}/token <br>
Method : POST <br>
RequestBody: {customer: Customer, services: [Services]} <br>
Response : Token <br>

For creating token for new customer
</li>

<li>URL: /branches/{branchId}/token/{tokenId}/complete <br>
Method : POST <br>
RequestHeader: {"userId": String}<br>
Response : {SUCCESS: String} <br>

For marking a token as completed
</li>

<li>URL: /branches/{branchId}/token/{tokenId}/cancel <br>
Method : POST <br>
RequestHeader: {"userId": String}<br>
Response : {SUCCESS: String} <br>

For marking a token as cancelled
</li>
</ol>

Other APIs supported, please refer source/documentation for details
<ol>
<li> /branches/{branchId}/refresh </li>
<li> /branches </li>
<li> /branches </li>
<li> /branches/{branchId}/services/{type} </li>
<li> /branches/{branchId}/manager/{managerId} </li>
<li> /branches/{branchId}/counters </li>
<li> /branches/{branchId}/counters </li>
<li> /branches/{branchId}/counters/{counterId}/operator/{operatorId} </li>
<li> /branches/{branchId}/counters/{counterId} </li>
<li> /branches/{branchId}/counters/{counterId}/steps </li>
<li> /services/{id} </li>
<li> /services </li>
<li> /services </li>
<li> /serviceSteps </li>
<li> /serviceSteps/{id} </li>
<li> /serviceSteps </li>
<li> /services/{id}/steps </li>
<li> /users </li>
<li> /users/{id} </li>
<li> /users </li>
<li> /users/{id}/roles </li>
<li> /users/{id}/roles </li>
<li> /roles </li>
</ol>

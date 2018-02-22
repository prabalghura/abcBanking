# abcBanking
API to provide managing token queues in a Banking system

API documentation available at https://prabalghura.github.io/abcBanking/

Below is the DB Model for the solution:

[
![abcbanking model](https://user-images.githubusercontent.com/34856263/36524251-5dbc46d2-17cb-11e8-96f8-2d70824c1985.png)
]

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

## Caching structre

<h3>Model JSON structure</h3>
<ol>
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
<ol>
<li>URL: /branches <br>
Method : GET <br>
Response : [Branch]<br>

For getting all the branches registered in the system.
</li>

</ol>

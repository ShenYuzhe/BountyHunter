App Side
1. In Our App We have four main views
1) Home
This page is responsible for searching nearby tasks and system-recommended tasks.
2) Post
This page is responsible for the tracking status of the posted or accepted tasks
as well as the corresponding price or hunter profile information.
3) Chat
This page is the aimed for voice or message chatting.
4) Setting
Setting parameters for the App.

Server Side
1) Rest API
Defines all interfaces called by front end and is split into two parts:
Profile Rest and Task Profile.

2) Rest Implementations
The transition part between Rest API and lower level functionalities.
It resolves input data, calls midwares, corresponding functionalities and then 
compose proper response messages.

3) DynamoDB
DynamoGeo manager
Utilize DynamoGeo Library to query DynamoDB for a given area range.

PosterHistory/HunterHistory/UserProfile
Manages tables for corresponding modules.

4) Recommendation
Utilize item-based recommendation algorithm to recommend tasks for users.
Put tags on each tasks and make a history record for each user tracking the tags of tasks
each user has finished. Recommendation is on the basis of these data.

User Stats
A DynamoDB used for tracking user history of task tags.


Web Side
A home page describing the Bounty Hunter App.
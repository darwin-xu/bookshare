|Action|Resource|Req Entity|Rsp Entity|Note|
|---|---|---|---|---|
|POST|/user/{user-id}/login|password|session-id|Login|
|GET|/user/{user-id}/genOneTimePwd|N/A|N/A|Generate the one time password for user|
|GET|/library/{book-isbn}|N/A|Book detail|Get the detail information|
|GET|/app/page/{page-name}|N/A|Column list|Get the column list of the page|
|GET|/app/page/{page-name}/{column-name}|N/A|Book list|Get the book list of the page|

{user-id} 用户ID，目前用用户的手机号：

Req:

/user/186163976453/login

Entity:

{
   "password":"abcd1234"
}

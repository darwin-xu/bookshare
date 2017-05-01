|Action|Resource|Req Entity|Rsp Entity|Note|
|---|---|---|---|---|
|POST|/users/getVerifyCode|{username}|N/A|Get the verify code for login|
|PATCH|/users/changePassword|{username, verifyCode}|N/A|Set the password by verifyCode|
|POST|/sessions/login|{username, password}|N/A|Use the user name and password to login|
|PATCH|/users/changePassword|{username, oldPassword, password}|N/A|Use the user name and old password to change the password|
|POST|/sessions/logout|{sessionID}|N/A|Logout the session|
|GET|/library/{book-isbn}|N/A|Book detail|Get the detail information|
|GET|/app/page/{page-name}|N/A|Column list|Get the column list of the page|
|GET|/app/page/{page-name}/{column-name}|N/A|Book list|Get the book list of the page|


###/user/{user-id}/login
{user-id} 用户ID，目前用用户的手机号

*****

Req:

/user/186163976453/login

Entity:

{
   "password":"abcd1234"
}

*****

Rsp:

{
    "session-id":"8b031194-2ad4-4607-b81b-443ca70afc37"
}

###/user/{user-id}/genOneTimePwd

*****

Req:

/user/186163976453/genOneTimePwd

Entity:

N/A

*****

Rsp:

N/A

###/library/{book-isbn}

*****

Req:

/library/9787500648192

Entity:

N/A

*****

Rsp:

{
    "reason":"查询成功",
    "result": {
             "levelNum":"8.0",
             "subtitle":"",
             "author":" 加西亚·马尔克斯",
             "pubdate":"2012-9-1",
             "origin_title":El amor en los tiempos del cólera",
             "binding":"精装",
             "pages":"401",
             "images_medium":"http://open.6api.net/mpic/s11284102.jpg",
             "images_large":"http://open.6api.net/lpic/s11284102.jpg",
             "publisher":"南海出版公司",
             "isbn10":"7544258971",
             "isbn13":"9787544258975",
             "title":"霍乱时期的爱情",
             "summary":"《霍乱时期的爱情》是加西亚•马尔克斯获得诺贝尔文学奖之后完成的第一部小说。",
             "price":"39.50元"
          },
	"error_code":0
}

###/app/page/{page-name}

*****

Req:

/app/page/main

Entity:

N/A

*****

Rsp:

{ "columns": ["经典", "体育", "财经", "少儿"] }

###/app/page/{page-name}

*****

Req:

/app/page/main/体育

Entity:

N/A

*****
Rsp:

{ "books": ["9787516810941", "9787509766989", "9787553805900", "9787550278998", "9787508665450", "9787301268711"] }

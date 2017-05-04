
INSERT INTO sheet(sheet_Name, section_Name) VALUES('Library', '热门')
INSERT INTO sheet(sheet_name, section_Name) VALUES('Library', '经典')
INSERT INTO sheet(sheet_name, section_Name) VALUES('Library', '流行')
INSERT INTO sheet(sheet_name, section_Name) VALUES('Library', '青春')
INSERT INTO sheet(sheet_name, section_Name) VALUES('Private', '悬疑')
INSERT INTO sheet(sheet_name, section_Name) VALUES('Private', '凶杀')

INSERT INTO section(section_name, isbn) VALUES('热门', '9787500648192')
INSERT INTO section(section_name, isbn) VALUES('热门', '9787505417731')
INSERT INTO section(section_name, isbn) VALUES('热门', '9787508622545')
INSERT INTO section(section_name, isbn) VALUES('热门', '9787301150894')
INSERT INTO section(section_name, isbn) VALUES('经典', '9787516810941')
INSERT INTO section(section_name, isbn) VALUES('经典', '9787509766989')
INSERT INTO section(section_name, isbn) VALUES('经典', '9787553805900')
INSERT INTO section(section_name, isbn) VALUES('经典', '9787550278998')
INSERT INTO section(section_name, isbn) VALUES('经典', '9787508665450')
INSERT INTO section(section_name, isbn) VALUES('经典', '9787301268711')
INSERT INTO section(section_name, isbn) VALUES('流行', '9787532772322')
INSERT INTO section(section_name, isbn) VALUES('流行', '9787540477783')
INSERT INTO section(section_name, isbn) VALUES('流行', '9787203079729')
INSERT INTO section(section_name, isbn) VALUES('流行', '9787108056153')
INSERT INTO section(section_name, isbn) VALUES('流行', '9787308161459')
INSERT INTO section(section_name, isbn) VALUES('青春', '9787540478025')
INSERT INTO section(section_name, isbn) VALUES('青春', '9787539962887')
INSERT INTO section(section_name, isbn) VALUES('青春', '9787515804743')
INSERT INTO section(section_name, isbn) VALUES('青春', '9787508654812')

INSERT INTO book(title, isbn10, isbn13, author, pages, publisher, price, summary) VALUES ('Clean Code', '10', '111', 'Kevin', 300, 'bookshare', 100.00, 'good book')
INSERT INTO book(title, isbn10, isbn13, author, pages, publisher, price, summary) VALUES ('Refactoring', '10', '222', 'Darwin', 300, 'bookshare', 100.00, 'good book')
INSERT INTO book(title, isbn10, isbn13, author, pages, publisher, price, summary) VALUES ('Agile Learning', '10', '333', 'Allen', 300, 'bookshare', 100.00, 'good book')

INSERT INTO category(name) VALUES ('Science');
INSERT INTO category(name) VALUES ('Art');
INSERT INTO category(name) VALUES ('Economy');
INSERT INTO category(name) VALUES ('Politics');
INSERT INTO category(name) VALUES ('Literature');
INSERT INTO category(name) VALUES ('Military');

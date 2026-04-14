# АВТОЛИЗИНГ
# Описание.
-----------------------------------------------------------------------------------------
Приложение, которое имитирует автолизинг.

Использованы основные HTTP-методы, созданы API эндпоинты для создания, редактирования, удаления, просмотра информации о машинах, клиентах, договоров и платежах.

Используется Kafka для передачи запросов POST, DELETE, UPDATE от контроллера к базе данных.

Настроен Spring Security через http.Basic(). Данные пользователей хранятся в базе данных. Есть возможность запросить информацию о роли пользователя, админ может добавлять новых пользователей и смотреть профили всех пользователей.
#     API эндпоинты
# Пользователи (/api/users)

○	POST /add  - добавить пользователя (доступно только админу)

       { 
       "username": "nikky",
       "role": "USER",
       "password": "{noop}444",
       "enabled": true
        }
○	GET /profiles - список всех пользователей (доступно только админу)

○	GET /current-user - посмотреть роль пользователя

#	Автомобили (/api/cars):

○	POST / - добавить автомобиль

       { 
        "vin": "UUURRREEEWWWQQQT1",
        
        "brand": "rtbsrt",
        
        "model": "erthser",
        
        "yearOfRelease": 2013,
        
        "cost": 23425345 
        }


○	GET / - список всех автомобилей

○	GET /search - поиск по критериям (марка, модель, год)

/api/cars/search?brand=tbsrt&model=erthser&yearOfRelease=2014

○	GET /available - список доступных авто

/api/cars/available

#	Клиенты (/api/clients):

○	POST / - добавить клиента

       {
        "fullName": "наовен",
        
        "passportNumber": "2223334445",
        
        "telephone": "+345734985"
        }

○	GET / - список клиентов

○	GET /search - поиск по ФИО/паспорту/телефону

/api/clients/search?name=наовен1&passport=2223332445&telephone=+34573498567

#	Договоры (/api/contracts):

○	POST / - создать договор

       {
        "carVIN": "PPPPPPRRREEEWWWQ1",
        
        "clientId": 1,
        
        "period": 22,
        
        "initialPayment": 234564,
        
        "percent": 27
       }

○	GET /active - активные договоры

○	GET /client/{clientId} - история договоров клиента

○	PATCH /{number}/close - закрыть договор


#	Платежи (/api/payments):

○	POST / - зарегистрировать платеж

       { 
        "contractId": 1,
        
        "amount": 23455
        }

○	GET /contract/{number} - платежи по договору

# Развертывание.
-----------------------------------------------------------------------------------------
[Клонируйте репозиторий](https://github.com/Ksenni888/javaBellSchoolSeptemer25_GalichkinaK/tree/task5-Spring) и запустите приложение в IntelliJ IDEA.

# Версии.
-----------------------------------------------------------------------------------------
![Static Badge](https://img.shields.io/badge/21%20-%20green?label=java%20version)
![Static Badge](https://img.shields.io/badge/4.0.0%20-%20green?label=maven)
![Static Badge](https://img.shields.io/badge/16%20-%20green?label=postgreSQL)
![Static Badge](https://img.shields.io/badge/3.5.6%20-%20green?label=SpringBootL)

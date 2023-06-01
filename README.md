# Прототип автоматизированной системы расчета вознаграждения контрагентов


## Условия

Все сервисы системы написаны на `Java SE 17`

Для проверки установленной версии Java выполните следующую команду:

```
java -version
```

## Описание API

### Сервис регистрации контрагентов и точек обслуживания

<details>
 <summary><code>POST</code> <code><b>/profile-point/profile</b></code> <code>(Создать профиль контрагента)</code></summary>

##### Тело запроса 

<table>
  <tr>
    <th> имя </th>
    <th> тип </th>
    <th width="43%"> тип данных </th>
    <th> описание </th>
  </tr>
  <tr>
    <td> name </td>
    <td> <b>обязательноe</b> </td>
    <td> string </td>
    <td> Наименование агентской организации </td>
  </tr>
  <tr>
    <td> profileType </td>
    <td> <b>обязательноe</b> </td>
    <td> string (enum - DISTRIBUTOR/PAYMENT_PARTNER) </td>
    <td> Наименование агентской организации </td>
  </tr>
  <tr>
    <td> status </td>
    <td> необязательное </td>
    <td> string (enum - <b>INACTIVE</b>/ACTIVE/SUSPENDED) </td>
    <td> Состояние профиля контрагента (<b>неактивный</b>/активный/заблокированный) </td>
  </tr>
</table>
<b>жирным</b> - значение по-умолчанию

##### Ответы

> | HTTP-код      | content-type        | описание                 |
> |---------------|---------------------|--------------------------|
> | `201`         | `application/json`  | `Профиль создан успешно` |
> | `400`         | `application/json`  | `Неверное тело запроса`  |

##### Пример cURL

> ```javascript
>  curl -X POST --H 'Content-Type: application/json' --data '{"name": "Тестовый Профиль", "profileType": "DISTRIBUTOR"} 'http://{{ingress_domain}}:80/profile-point/profile' 
> ```
  
</details>

<details>
 <summary><code>GET</code> <code><b>/profile/{profileId}</b></code> <code>(Получить информацию о профиле контрагента по его ID)</code></summary>

##### Ответы

> | HTTP-код      | content-type        | описание                                     |
> |---------------|---------------------|----------------------------------------------|
> | `200`         | `application/json`  | `Профиль с указанным ID найден`              |
> | `400`         | `application/json`  | `Указанный ID не соответствует формату UUID` |
> | `404`         | `application/json`  | `Профиль с указанным ID не найден`           |

##### Пример cURL

> ```javascript
>  curl -X GET 'http://{{ingress_domain}}:80/profile-point/profile/{{profile_id}}'
> ```
  
</details>

## Запуск тестов

Для запуска тестов запустите следующую команду из корня проекта

```bash
mvn test -f pom.xml
```

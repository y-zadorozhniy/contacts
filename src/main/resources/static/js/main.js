$.notify.defaults({
    globalPosition: 'bottom left',
})

function deleteRecord(id) {
    $.ajax({
        url: "/api/record/" + id,
        method: "DELETE",
        dataType: "json"
    }).done(function (response) {
        if (response.status === "APPROVED") {
            table.row("#record" + id).remove().draw();
        }
        $.notify(response.message, "success");
    }).fail(function (response) {
        if (response.status === 404) {
            $.notify("Запись отсутствует. Обновите страницу", "info");
        } else if (response.status === 403) {
            $.notify("Отсутствуют права на удаление записи", "warn");
        } else {
            $.notify("Произошла ошибка при удалении записи", "error");
        }
    });
}

function addContact() {
    let contactBlock = $(".edit-contact-first-contact").clone();
    contactBlock.removeClass("edit-contact-first-contact")
        .find("input[type=text]").val("")
        .find("select").val("PHONE");
    contactBlock.insertBefore($("#edit-contact-contacts-controlls"));
    setContactPlaceholder();
}

function removeLastContact() {
    if ($(".edit-contact-contact").length > 1) {
        $(".edit-contact-contact").last().remove();
    }
}

function setContactPlaceholder() {
    const contactsPlaceholder = {
        PHONE: "+7 (999) 999-99-99",
        EMAIL: "adress@domain.name",
        VK: "https://vk.com/",
        INSTAGRAM: "https://www.instagram.com/",
        ODNOKLASSNIKI: "https://ok.ru/",
        FACEBOOK: "https://www.facebook.com/",
        LINKEDIN: "https://www.linkedin.com/",
        OTHER: ""
    };
    $(".edit-contact-contact").each(function (index) {
        let type = $(this).children(".edit-contact-contact-type").val();
        $(this).children(".edit-contact-contact-value").attr('placeholder', contactsPlaceholder[type]);
    });
}

function submitContact() {
    let contactContacts = [];
    $(".edit-contact-contact").each(function (index) {
        let c = {
            type: $(this).children(".edit-contact-contact-type").val(),
            value: $(this).children(".edit-contact-contact-value").val(),
            comment: $(this).children(".edit-contact-contact-comment").val()
        }
        contactContacts.push(c)
    });
    let tags = $("#edit-contact-tags").val()
        .replace(/Ё/g, 'Е')
        .replace(/ё/g, 'е')
        .split("\n");
    const contact = {
        fullName: $("#edit-contact-fullname").val(),
        company: $("#edit-contact-company").val(),
        occupation: $("#edit-contact-occupation").val(),
        country: $("#edit-contact-country").val(),
        city: $("#edit-contact-city").val(),
        contacts: contactContacts,
        tags: tags,
        comment: $("#edit-contact-comment").val()
    }
    let recordId = $("#edit-contact-record-id").val();
    $.ajax({
        url: recordId != null ? "/api/record/" + recordId : "/api/record",
        method: "POST",
        dataType: "json",
        contentType: 'application/json',
        data: JSON.stringify(contact)
    }).done(function () {
        $(location).attr('href', '/');
    }).fail(function (response) {
        let message;
        if (response && response.responseJSON.message) {
            message = response.responseJSON.message;
        } else {
            message = "Произошла ошибка при добавлении записи";
        }
        $.notify(message, "error");
    });
}

function declineRequest(requestId, redirect) {
    let dialog = $("#request-decline-dialog").dialog({
        autoOpen: false,
        modal: true,
        height: 220,
        width: 300,
        resizable: false,
        buttons: {
            'Отклонить': function () {
                const data = {
                    comment: $("#request-decline-comment").val()
                }
                $.ajax({
                    url: "/api/request/" + requestId,
                    method: "PUT",
                    dataType: "json",
                    contentType: 'application/json',
                    data: JSON.stringify(data)
                }).done(function (response) {
                    if (redirect) {
                        $(location).attr('href', '/requests');
                    } else {
                        table.row("#request" + requestId).remove().draw();
                        $.notify(response.message, "success");
                    }
                }).fail(function (response) {
                    if (response.status === 404) {
                        $.notify("Запрос отсутствует. Обновите страницу", "info");
                    } else if (response.status === 403) {
                        $.notify("Отсутствуют права на модерацию правок", "warn");
                    } else {
                        $.notify("Произошла ошибка при отмене правки", "error");
                    }
                });
                dialog.dialog("close");
            }
        }
    });
    dialog.dialog("open");
}

function acceptRequest(requestId, redirect) {
    $.ajax({
        url: "/api/request/" + requestId + "/accept",
        method: "POST",
        dataType: "json"
    }).done(function (response) {
        $(location).attr('href', '/requests');
    }).fail(function (response) {
        if (response.status === 404) {
            $.notify("Запрос отсутствует. Обновите страницу", "info");
        } else if (response.status === 403) {
            $.notify("Отсутствуют права на модерацию правок", "warn");
        } else {
            $.notify("Произошла ошибка при обработке правки", "error");
        }
    });
}

function submitUser() {
    let userId = $("#edit-user-user-id").val();
    let username = userId ? null : $("#edit-user-username").val();
    const user = {
        username: username,
        email: $("#edit-user-email").val(),
        role: $("#edit-user-role").val(),
        password: $("#edit-user-password").val(),
        passwordRepeat: $("#edit-user-password-repeat").val()
    }
    let isUserCorrect = validateUser(user, !userId);
    if (isUserCorrect) {
        $.ajax({
            url: userId ? "/api/user/" + userId : "/api/user",
            method: "POST",
            dataType: "json",
            contentType: 'application/json',
            data: JSON.stringify(user)
        }).done(function () {
            $(location).attr('href', '/users');
        }).fail(function (response) {
            let message;
            if (response && response.responseJSON.message) {
                message = response.responseJSON.message;
            } else {
                message = "Произошла ошибка при " + (userId ? "редактировании" : "добавлении") +
                    " пользователя";
            }
            $.notify(message, "error");
        });
    }
}

function validateUser(user, isNewUser) {
    if (isNewUser && !user.username) {
        $.notify("Поле \"Имя пользователя\" обязательно для заполнения", "error");
        return false;
    }
    if (!user.email) {
        $.notify("Поле \"Почта\" обязательно для заполнения", "error");
        return false;
    }
    if (isNewUser && !user.password) {
        $.notify("Поле \"Пароль\" обязательно для заполнения", "error");
        return false;
    }
    if (user.password !== user.passwordRepeat) {
        $.notify("Введенные пароли не совпадают", "error");
        return false;
    }
    return true;
}

function removeUser(userId) {
    $.ajax({
        url: "/api/user/" + userId,
        method: "DELETE",
        dataType: "json"
    }).done(function (response) {
        if (response.status === "APPROVED") {
            $(location).attr('href', '/users');
        }

    }).fail(function (response) {
        if (response.status === 404) {
            $.notify("Пользователь не найден. Обновите страницу", "info");
        } else if (response.status === 403) {
            $.notify("Отсутствуют права на удаление пользователя", "warn");
        } else {
            $.notify("Произошла ошибка при удалении пользователя", "error");
        }
    });
}
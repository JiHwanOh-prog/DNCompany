export function patchMessageListFrom(usersId, messageListObj, callback) {
    fetch(`/api/v1/messageList/${usersId}/listFrom`, {
        method: 'PATCH',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(messageListObj)
    })
        .then(resp => {
            if (resp.ok && callback) {
                callback();
            }
        });
}

export function patchMessageListTo(usersId, messageListObj, callback) {
    fetch(`/api/v1/messageList/${usersId}/listTo`, {
        method: 'PATCH',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(messageListObj)
    })
        .then(resp => {
            if (resp.ok && callback) {
                callback();
            }
        });
}
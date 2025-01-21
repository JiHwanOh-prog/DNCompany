// DOM 객체
const $form = document.querySelector('.login__input');
const elInputId = document.querySelector("#loginId");
const elInputPassword = document.querySelector("#password");
const elInputSaveId = document.querySelector("#saveId");

// 저장된 아이디가 있으면 불러오기 (localStorage 사용)
const savedLoginId = localStorage.getItem('savedLoginId');
if (savedLoginId) {
    elInputId.value = savedLoginId;
    elInputSaveId.checked = true;
}

// 폼 제출
$form.addEventListener('submit', function (e) {
    // 아이디 저장 처리
    if (elInputSaveId.checked) {
        localStorage.setItem('savedLoginId', elInputId.value);
    } else {
        localStorage.removeItem('savedLoginId');
    }

    // 입력값 검증
    if (!elInputId.value.trim()) {
        e.preventDefault();
        alert('아이디를 입력해주세요.');
        elInputId.focus();
        return;
    }

    if (!elInputPassword.value) {
        e.preventDefault();
        alert('비밀번호를 입력해주세요.');
        elInputPassword.focus();
        return;
    }
});

// {   // 아이디 저장
//     const $saveIdBox = document.querySelector('#saveId');
//
//     $saveIdBox.addEventListener('change', (e) => {
//         console.log($saveIdBox.value);
//     });
//
//     console.log('hi')
//
//
//
// }
















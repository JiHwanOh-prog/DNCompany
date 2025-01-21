import * as likeApi from "./modules/likeApi.js";
import * as userApi from "../user/modules/userApi.js";
import * as messageListApi from "../user/modules/messageListApi.js";

{
    const $favoriteBtn = document.querySelector('.favorite_button');
    const $deleteBtn = document.querySelector('.delete_Btn_item');

    $deleteBtn?.addEventListener('click', function () {
        if (confirm('정말 삭제하시겠습니까?')) {
            console.dir(this)
            const dnId = this.dataset.dnId;
            const productId = this.dataset.productId;
            location.href = `/dn/delete?dnId=${dnId}&productId=${productId}`;
        }
    });

    {// 진입과 동시에 좋아요 체크
        likeApi.checkLike(dnId, function (data) {
            console.log(data); // 진입과 동시에 어떤 데이터가 나오는지 확인하는 로그
            // 전달받은 데이터를 활용하여 좋아요 버튼의 상태와 좋아요 수 반영하기
            const $favoriteCount = document.querySelector('.favorite_count');
            $favoriteCount.textContent = data.likeCount; // 전달받은 data에 있는 likecount를 넘겨줌

            if(data.liked) {// trusy : 데이터가 있으면 true 없으면 false
                $favoriteBtn.classList.add('active');
            } else {
                $favoriteBtn.classList.remove('active');
            }

        })
    }

    // 좋아요 버튼 클릭
    $favoriteBtn.addEventListener('click', function () {
        // this.classList.toggle('active'); // classList는 클래스를 추가하는 것. 조건에 따라 active를 토글하도록 변경
        const $favoriteCount = this.querySelector('.favorite_count');
        // const currentCount = parseInt($likeCount.textContent); // 토글 처리할 때 사용한 코드

        // 서버에 좋아요 토글 처리, 반영된 결과 다시 조회해서 화면 처리
        likeApi.toggleLike(dnId, function (data) {
            console.log(data);
            if (!data.success) {
                console.log(data.message);
                alert(data.message);
                return;
            }

            if (data.liked) {
                $favoriteBtn.classList.add('active');
            } else {
                $favoriteBtn.classList.remove('active');
            }

            $favoriteCount.textContent = data.likeCount;

        })
    });
}


openModal();

function openModal() {
    const $messageSend = document.querySelector('.chat_button');
    const $openModal = document.querySelector('.message_modal_box');
    const $closeModal = document.querySelector('.message_btn_close');
    const $form = document.querySelector('.message_form');
    const $messageUserTo = document.querySelector("#loginIdTo");


    // 모달 팝업 열기
    $messageSend.addEventListener('click', function () {
        console.log(usersId)
        if(!(usersId == null)) {
            $openModal.style.display = 'flex';
        } else {
            alert("로그인이 필요한 서비스입니다!")
            window.location.href = "/user/login";
        }
    });

// 모달 팝업 닫기
    $closeModal.addEventListener('click', function () {
        $openModal.style.display = 'none';
    });

// 아이디 중복 검사 및 폼 제출 처리
    function handleUsernameValidation(username, callback) {
        if (!username.trim()) {
            alert("아이디를 입력하세요!");  // 아이디가 비어있는 경우 알림 표시
            callback(false);  // callback을 통해 false 반환
            return;
        }

        // 아이디 중복 검사 API 호출
        userApi.checkLoginId(username, function (data) {  // userApi에서 checkLoginId 호출
            if (data && data.exists !== undefined) {  // data.exists가 정상적으로 존재하는지 확인
                if (data.exists) {
                    callback(true);  // 중복된 아이디일 경우 true 반환
                } else {
                    alert("아이디를 다시 입력하세요.");
                    callback(false);  // 중복되지 않은 아이디일 경우 false 반환
                }
            } else {
                alert("API 응답이 잘못되었습니다. 다시 시도해 주세요.");
                callback(false);  // API 응답이 예상과 다를 경우 처리
            }
        });
    }

// 폼 제출 시 아이디 중복 여부 확인 후 쪽지 전송 처리
    $form.addEventListener('submit', function (e) {
        e.preventDefault();  // 기본 폼 제출 방지

        const value = $messageUserTo.value.trim();  // 입력값에서 공백 제거
        console.log("입력된 아이디:", value);  // 디버깅

        handleUsernameValidation(value, function (isUsernameValid) {  // 아이디 중복 검사 결과
            if (isUsernameValid) {
                alert("쪽지 전송 완료!");
                // 폼을 실제로 제출 (AJAX로 처리하거나 폼 전송을 해도 됩니다)
                // 서버로 데이터를 보내는 방법을 추가하세요.
                $form.submit();  // submit 호출로 폼을 전송 (아이디 중복 확인 후)
            } else {
                $messageUserTo.value = "";  // 입력 필드 초기화
                $messageUserTo.focus();  // 아이디 입력 필드로 포커스 이동
            }
        });
    });
}

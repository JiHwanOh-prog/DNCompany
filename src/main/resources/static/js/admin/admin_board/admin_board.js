document.addEventListener("DOMContentLoaded", function () {
    // URL 파라미터 처리
    const urlParams = new URLSearchParams(window.location.search);
    const category = urlParams.get('category');
    if (category) {
        const boardSelect = document.getElementById("boardSelect");
        if (boardSelect) {
            boardSelect.value = category;
        }
    }


    // userImage 클릭 이벤트
    const userImage = document.getElementById("userImage");
    if (userImage) {
        userImage.addEventListener("click", function () {
            const userModal = document.getElementById("userModal");
            if (userModal) {
                userModal.classList.toggle("show");
            } else {
                console.error("User modal not found");
            }
        });
    }

    // 마이페이지 버튼 클릭 이벤트
    const myPageButton = document.getElementById("myPageButton");
    if (myPageButton) {
        myPageButton.addEventListener("click", function () {
            window.location.href = "/mypage";
        });
    }

    // 전체 선택/해제
    const selectAllCheckbox = document.getElementById("selectAll");
    selectAllCheckbox.addEventListener("change", function () {
        const checkboxes = document.querySelectorAll(".post-checkbox");
        checkboxes.forEach((checkbox) => {
            checkbox.checked = selectAllCheckbox.checked;
        });
    });

    // 게시물 데이터 조회 함수 수정
    async function fetchPosts(category, page = 1) {
        try {
            const form = new FormData();
            form.append('category', category);
            form.append('page', page);
            form.append('size', document.getElementById('itemsPerPage').value);

            const response = await fetch("/admin/board/list/reportBoard", {
                method: "POST",
                body: form
            });

            if (!response.ok) {
                throw new Error('Failed to fetch posts');
            }

            const html = await response.text();
            // 임시 div를 만들어 HTML 파싱
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;

            // postListBody 영역 전체를 교체
            const postListBody = document.getElementById('postListBody');
            if (postListBody) {
                postListBody.innerHTML = tempDiv.querySelector('#postListBody').innerHTML;
            }
        } catch (error) {
            console.error('Error:', error);
        }
    }

    // 검색 폼 제출 처리도 위와 같은 방식으로 수정
    searchForm.addEventListener("submit", async function(e) {
        e.preventDefault();
        const formData = new FormData(this);
        formData.append('page', '1');
        formData.append('size', document.getElementById('itemsPerPage').value);

        try {
            const response = await fetch("/admin/board/list/reportBoard", {
                method: "POST",
                body: formData
            });

            if (!response.ok) throw new Error('검색 실패');

            const html = await response.text();
            const tempDiv = document.createElement('div');
            tempDiv.innerHTML = html;

            const postListBody = document.getElementById('postListBody');
            if (postListBody) {
                postListBody.innerHTML = tempDiv.querySelector('#postListBody').innerHTML;
            }
        } catch (error) {
            console.error('Error:', error);
        }
    });

    // 페이지 변경 함수
    window.changePage = function(page) {
        const formData = new FormData(document.getElementById('searchForm'));
        formData.append('page', page);
        formData.append('size', document.getElementById('itemsPerPage').value);

        fetch("/admin/board/list/reportBoard", {
            method: "POST",
            body: formData
        })
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.text();
            })
            .then(html => {
                const tempDiv = document.createElement('div');
                tempDiv.innerHTML = html;

                const postListBody = document.getElementById('postListBody');
                if (postListBody) {
                    postListBody.innerHTML = tempDiv.querySelector('#postListBody').innerHTML;
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    };

    // 날짜 버튼 클릭 처리
    const dateButtons = document.querySelectorAll('.date-btn');
    dateButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            const today = new Date();
            const endDate = document.getElementById('endDate');
            const startDate = document.getElementById('startDate');

            // 종료일은 항상 오늘
            endDate.valueAsDate = today;

            // 시작일 계산
            switch(this.textContent) {
                case '오늘':
                    startDate.valueAsDate = today;
                    break;
                case '3일':
                    startDate.valueAsDate = new Date(today.setDate(today.getDate() - 3));
                    break;
                case '7일':
                    startDate.valueAsDate = new Date(today.setDate(today.getDate() - 7));
                    break;
                case '1개월':
                    startDate.valueAsDate = new Date(today.setMonth(today.getMonth() - 1));
                    break;
            }
        });
    });

    // 페이지당 게시물 수 변경 이벤트 수정
    const itemsPerPageSelect = document.getElementById("itemsPerPage");
    itemsPerPageSelect.addEventListener("change", function () {
        const formData = new FormData(document.getElementById('searchForm'));
        formData.append('page', '1');  // 페이지 1로 리셋
        formData.append('size', this.value);

        fetch("/admin/board/list/reportBoard", {
            method: "POST",
            body: formData
        })
            .then(response => {
                if (!response.ok) throw new Error('Network response was not ok');
                return response.text();
            })
            .then(html => {
                document.getElementById("postListBody").innerHTML = html;
            })
            .catch(error => {
                console.error('Error:', error);
            });
    });

    // 초기 데이터 로드
    fetchPosts(category || "all", 1);
});


// ==========================================================================================================================
// 모달 코드

document.addEventListener('DOMContentLoaded', function() {
    const postListBody = document.getElementById('postListBody');
    const postDetailModal = document.getElementById('postDetailModal');
    const closeModalBtn = document.querySelector('.close-btn');
    const submitReplyBtn = document.getElementById('submitReplyBtn');

    // 모달 외부 클릭 시 모달 닫기
    postDetailModal.addEventListener('click', function(e) {
        if (e.target === postDetailModal) {
            postDetailModal.style.display = 'none';
        }
    });

    // 제목 또는 답변처리 칸 클릭 시 모달 오픈
    postListBody.addEventListener('click', function(e) {
        const titleCell = e.target.closest('td:nth-child(4)');
        const statusCell = e.target.closest('td:nth-child(5)');

        if (titleCell || statusCell) {
            const row = e.target.closest('tr');
            const postId = row.getAttribute('data-post-id');
            const category = row.getAttribute('data-category');


            // QnA가 아닐 경우 얼럿 표시하고 반환
            // 임시로 나머지 카테고리 설정 안해놈
            // 그래서 강제로 막기 처리
            if (category !== 'QNA') {
                alert('QnA 게시글만 조회할 수 있습니다.');
                return;
            }

            console.log("Clicked row data:", { postId, category }); // 디버깅용

            // 모달에 정보 채우기
            document.getElementById('modalPostCategory').textContent = category;
            document.getElementById('modalPostTitle').textContent = row.querySelector('td:nth-child(4)').textContent;
            document.getElementById('modalPostWriter').textContent = row.querySelector('td:nth-child(6)').textContent;
            document.getElementById('modalPostDate').textContent = row.querySelector('td:nth-child(9)').textContent;

            // 현재 게시글 정보를 모달에 저장
            postDetailModal.setAttribute('data-post-id', postId);
            postDetailModal.setAttribute('data-category', category);

            // QnA 게시글일 경우 내용과 답변 로드
            if (category === 'QNA') {
                loadQnaDetail(postId);
                loadReplies(postId, category);
            }

            postDetailModal.style.display = 'block';
        }
    });

    // QnA 상세 내용 로드 함수
    async function loadQnaDetail(qnaId) {
        try {
            console.log("QnA ID:", qnaId);
            const response = await fetch(`/admin/board/qna/detail?qnaId=${qnaId}`);
            if (!response.ok) throw new Error('상세 내용 로드 실패');
            const data = await response.json();
            console.log("받은 데이터:", data);  // 서버에서 받은 데이터 확인

            // 내용이 있는지 확인
            // 아니 이녀석 별칭도 줬는데 왜 대문자로 쳐 오는거지 ?
            // 대문자로 쳐와서 내용값을 못 찾았음
            // 위에 로그는 전부 COnTENT 찾을려고 설정해논 것들
            if (data && data.CONTENT) {
                document.getElementById('modalPostContent').textContent = data.CONTENT;
            } else {
                console.log("내용 없음:", data);
                document.getElementById('modalPostContent').textContent = '내용이 없습니다.';
            }
        } catch (error) {
            console.error('상세 내용 로드 실패:', error);
            document.getElementById('modalPostContent').textContent = '내용을 불러오지 못했습니다.';
        }
    }

    // 답변 로드 함수
    async function loadReplies(postId, category) {
        const repliesContainer = document.getElementById('existingReplies');
        try {
            const response = await fetch(`/admin/board/replies?postId=${postId}&category=${category}`);
            const replies = await response.json();

            if (replies.length > 0) {
                repliesContainer.innerHTML = replies.map(reply => `
                       <div class="reply">
                           <p>${reply.qnaAnswerContent}</p>
                           <small>${new Date(reply.qnaAnswerCreatedAt).toLocaleString()}</small>
                       </div>
                   `).join('');
            } else {
                repliesContainer.innerHTML = `
                       <div class="reply">
                           <p>아직 등록된 댓글/답변이 없습니다.</p>
                       </div>
                   `;
            }
        } catch (error) {
            console.error('댓글 로드 실패:', error);
            repliesContainer.innerHTML = `
                   <div class="reply">
                       <p>댓글을 불러오는 중 오류가 발생했습니다.</p>
                   </div>
               `;
        }
    }

    // 답변 등록 버튼 이벤트
    submitReplyBtn.addEventListener('click', function() {
        const replyContent = document.getElementById('replyTextarea').value;
        const postId = postDetailModal.getAttribute('data-post-id');
        const category = postDetailModal.getAttribute('data-category');

        console.log("Submitting reply:", { postId, category, content: replyContent }); // 디버깅용

        // 상세 로깅 추가 버그잡기 쌍놈시키
        console.log("전송 데이터 확인:", {
            qnaId: postId,
            qnaAnswerContent: replyContent,
            category: category
        });

        if (!replyContent.trim()) {
            alert('답변 내용을 입력해주세요.');
            return;
        }

        if (category === 'QNA') {
            const requestData = {
                qnaId: Number(postId),
                category: category,
                qnaAnswerContent: replyContent
            };

            console.log('서버로 전송할 데이터:', requestData); // 로그 추가 모르겠다 버그야~~ 그만 하고 나와라~~~

            fetch('/admin/board/reply', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                    body: JSON.stringify(requestData)

            })
                .then(response => response.json())
                .then(data => {
                    if (data.error) throw new Error(data.error);
                    alert('답변이 등록되었습니다.');
                    loadReplies(postId, category);
                    document.getElementById('replyTextarea').value = '';
                    const statusCell = document.querySelector(`tr[data-post-id="${postId}"] td:nth-child(5)`);
                    if (statusCell) {
                        statusCell.textContent = 'O';
                    }
                })
                .catch(error => {
                    console.error('답변 등록 실패:', error);
                    alert('답변 등록에 실패했습니다: ' + error.message);
                });
        }
    });

    // 모달 닫기 버튼
    closeModalBtn.addEventListener('click', function() {
        postDetailModal.style.display = 'none';
    });
});

//===================================================== 보드 삭제 ==================================================================================

/// postListBody에 이벤트 위임을 사용하여 삭제 버튼 이벤트 처리
/*
    왜 이벤트 위임을 했나 ? 처음에 이벤트 리스너는 조건이 페이지 로드 후 다 하지만 동적으로 가져오는 버튼인 delete-button은
    처음 진입시에 이벤트가 걸렸지만 동적으로 로드된 데이터내용에서 새로 생겼고 이벤트리스너는 바보 처럼 이름이 같아도 찾지 못한다
    그래서 동적 내용인 @postListBody에 이벤트를 위임하고
    if (e.target.classList.contains('delete-button')) {
     을 추가해서 delete-button 버튼을 활성화 했다 기존 이벤트 와 다른점은 if로 버튼을 활성화 한 것과 요소에
     DOMContentLoaded 대신postListBody가 들어간거 뿐
      기존 이벤트 리스너
    1. 페이지 최초 로드
    2. .delete-button에 이벤트 리스너 등록
    3. Ajax로 새 데이터 로드
    4. postListBody 내용 교체
    5. 새로운 .delete-button 생성
    6. 버튼 클릭 -> 아무 일도 일어나지 않음 (이벤트 리스너가 없음)

    [이벤트 위임 사용]
    1. 페이지 최초 로드
    2. #postListBody에 이벤트 리스너 등록
    3. Ajax로 새 데이터 로드
    4. postListBody 내용 교체
    5. 새로운 .delete-button 생성
    6. 버튼 클릭 -> postListBody의 이벤트 리스너가 감지
    7. e.target이 .delete-button인지 확인
    8. 맞다면 삭제

    과정에 차이가 있다 이번에 배운 기술로 동적자료를 더 쉽게 다를 수 있고 이해가 높아진 것 같다
    사실상 이벤트 버블링을 활용한 케이스다 개념만 알고 있었지만 사용해보니 처음에 버튼이 안눌린 이유가 납득이 간다
    현제 html에서는 delete-button 누르면 과정에 이벤트가 있고 마지막에 대신postListBody에 도달한다
    활용한다면 특정 이벤트에 버블링을 멈추고 특정 코드만 실행시킬 수 있을 것 같다


 */
document.getElementById('postListBody').addEventListener('click', function(e) {
    // 클릭된 요소가 delete-button 클래스를 가진 경우에만 실행
    if (e.target.classList.contains('delete-button')) {
        console.log('삭제 버튼 클릭됨'); // 디버깅용 로그

        // 선택된 체크박스 확인
        const checkedBoxes = document.querySelectorAll('.post-checkbox:checked');
        console.log(checkedBoxes);

        if (checkedBoxes.length === 0) {
            alert('삭제할 게시글을 선택해주세요.');
            return;
        }

        if (!confirm('선택한 게시글을 삭제하시겠습니까?')) {
            return;
        }

        // 1. 빈 객체 생성과 동시에 각 카테고리별 빈 배열 초기화함
        const selectedPosts = {
            qnaIds: [],      // QnA 게시글 ID들이 들어갈 빈 배열
            reportIds: [],
            helpIds: [],
            eventIds: [],
            zipIds: [],
            dnIds: []
        };

        checkedBoxes.forEach(checkbox => {
            const row = checkbox.closest('tr');
            const postId = Number(row.dataset.postId);
            const category = row.dataset.category.toUpperCase();
            console.log('처리중인 게시글:', {postId, category}); // 디버깅용 로그

            // 카테고리에 따라 분류
            switch(category) {
                case 'QNA':
                    selectedPosts.qnaIds.push(postId); // QnA 배열에 추가 ex QnAid= 1 , 2 이면 배열에 1과2가 들어가서 배열안에[1,2] 들어가 있고 삭제 시 1,2가 삭제됨
                    break;
                case '신고':
                    selectedPosts.reportIds.push(postId);
                    break;
                case 'HELP':
                    selectedPosts.helpIds.push(postId);
                    break;
                case 'EVENT':
                    selectedPosts.eventIds.push(postId);
                    break;
                case 'ZIP':
                    selectedPosts.zipIds.push(postId);
                    break;
                case 'DN': 
                    selectedPosts.dnIds.push(postId);
                    break;
            }
        });

        console.log('전송할 데이터:', selectedPosts); // 디버깅용 로그

        // 서버로 삭제 요청 전송
        fetch('/admin/board/delete', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(selectedPosts)
        })
            .then(response => {
                console.log('서버 응답:', response); // 디버깅용 로그
                return response.json();
            })
            .then(result => {
                console.log('삭제 결과:', result);
                // 현재 검색 조건으로 목록 새로고침
                const formData = new FormData(document.getElementById('searchForm'));
                formData.append('page', '1');
                formData.append('size', document.getElementById('itemsPerPage').value);

                return fetch("/admin/board/list/reportBoard", {
                    method: "POST",
                    body: formData
                });
            })
            .then(response => response.text())
            .then(html => {
                const tempDiv = document.createElement('div');
                tempDiv.innerHTML = html;

                const postListBody = document.getElementById('postListBody');
                if (postListBody) {
                    postListBody.innerHTML = tempDiv.querySelector('#postListBody').innerHTML;
                }
                alert('선택한 게시글이 삭제되었습니다.');
            })
            .catch(error => {
                console.error('삭제 중 오류 발생:', error);
                alert('게시글 삭제 중 오류가 발생했습니다.');
            });
    }
});

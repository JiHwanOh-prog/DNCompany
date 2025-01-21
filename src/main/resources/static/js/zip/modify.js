$('#zipContent').summernote({
    placeholder: '내용을 수정해주세요',
    tabsize: 2,
    height: 500,
    toolbar: [
        ['style', ['style']],
        ['font', ['bold', 'underline', 'clear']],
        ['color', ['color']],
        ['para', ['ul', 'ol', 'paragraph']],
        ['table', ['table']],
        ['insert', ['link', 'picture']],
        ['view', ['fullscreen', 'codeview', 'help']]
    ]
});

document.addEventListener('DOMContentLoaded', () => {
    const $title = document.querySelector('.question_titleInput');
    const $content = document.querySelector('#zipContent');
    const $submitbtn = document.querySelector('.question_nosubmitBtn');

    $submitbtn.addEventListener('click', (e) => {
        e.preventDefault();

        const title = $title.value.trim();
        const content = $content.value.trim();

        if (!title) {
            alert('제목을 입력해주세요.');
            $title.value = '';
            $title.focus();
            return;
        }

        if (!content || content.length < 5) {
            alert('내용을 5자 이상 입력해주세요.');
            $content.value = '';
            $content.focus();
            return;
        }

        document.querySelector('form').submit();
    });
});
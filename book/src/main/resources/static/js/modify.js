// 삭제 버튼이 클릭이 되면 actionForm submit

document.querySelector(".btn-danger").addEventListener("click", ()=>{
    
    const actionForm = document.querySelector("#actionForm");
    actionForm.submit();
})




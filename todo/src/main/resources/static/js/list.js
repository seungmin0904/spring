// check box가 클릭이 되면 
// checkbox value 가져오기
// value="true",data-id=${dto.id} 값 가져오기

document.querySelector(".list-group").addEventListener("click", (e) =>  {
    // 이벤트가 발생한 지점 찾기
    const chk = e.target
    console.log(chk);
    // checkbox 체크,해제 확인
    console.log(chk.checked);
    // id 가져오기 
    // closest("선택자") : 부모에서 제일 가까운 요소 찾기
    // value="true",data-id=${dto.id} 값 가져오기
    // data- 속성값을 가져오기 : dataset.data속성값 
  const id = chk.closest("label").dataset.id;
  console.log(id);

  // actionForm 찾은 후 요소들의 value 값 변경
const actionForm = document.querySelector('#actionForm');
       actionForm.id.value = id;
       actionForm.completed.value = chk.checked;

       actionForm.submit();
});
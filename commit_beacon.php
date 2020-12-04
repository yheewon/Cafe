<?php

    error_reporting(E_ALL);
    ini_set('display_errors',1);

    include('dbcon.php');


    $android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");


    if( (($_SERVER['REQUEST_METHOD'] == 'POST') && isset($_POST['submit'])) || $android )
    {

        // 안드로이드 코드의 postParameters 변수에 적어준 이름을 가지고 값을 전달 받습니다.

        $id=$_POST['id'];
        $beacon=$_POST['beacon'];


        // 입력안된 항목이 있을 경우 에러 메시지 생
       if(empty($id)){
            $errMSG = "입력이 안됨.";
        }
        else if(empty($beacon)){
            $errMSG = "beacon 입력하세요.";
        }


        // 에러 메시지가 정의 안되어 있다면 이름과 나라 모두 입력된 경우
        if(!isset($errMSG))
        {
            try{
                // SQL문을 실행하여 데이터를 MySQL 서버의 person 테이블에 저장합니다.
                $stmt = $con->prepare('UPDATE reservation SET beacon = :beacon WHERE id = :id');
                $stmt->bindParam(':id', $id);
                $stmt->bindParam(':beacon', $beacon);



                //실행결과 메시지
                if($stmt->execute())
                {
                    $successMSG = "수정했습니다.";
                }
                else
                {
                    $errMSG = "사용자 추가 에러";
                }

            } catch(PDOException $e) {
                die("Database error: " . $e->getMessage());
            }
        }

    }

?>


<?php
    if (isset($errMSG)) echo $errMSG;
    if (isset($successMSG)) echo $successMSG;

	$android = strpos($_SERVER['HTTP_USER_AGENT'], "Android");

    if( !$android )
    {
?>
    <html>
       <body>

            <form action="<?php $_PHP_SELF ?>" method="POST">
                P_Num: <input type = "text" name = "pnum" />
                Name: <input type = "text" name = "name" />
                Room: <input type = "text" name = "room" />
                Time: <input type = "text" name = "time" />
                Drink: <input type = "text" name = "drink" />
                <input type = "submit" name = "submit" />
            </form>

       </body>
    </html>

<?php
    }
?>

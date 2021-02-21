<?php
require_once __DIR__ . '/app/Models/Account.php';
require_once __DIR__ . '/app/Models/Balance.php';
require_once __DIR__ . '/app/Models/CheckIn.php';
require_once __DIR__ . '/app/Models/Store.php';
require_once __DIR__ . '/app/Models/Favorite.php';

$page = $subpage;
switch( $page ){
    case 'account': include_once __DIR__ . '/account.php'; break;
    case 'store': include_once __DIR__ . '/store.php'; break;
    default: header("Location: /404.php");
}


?>
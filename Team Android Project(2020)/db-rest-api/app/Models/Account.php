<?php

namespace app\Models;

require_once __DIR__ . '/Model.php';

use Exception;
use mysqli_stmt;
use app\Utilities\MySqlHandler;

class Account extends Model
{
    protected $table = 'account';
    protected $key = 'account_id';

    protected $fillable = [
        'email'
    ];

    protected $cols = [
        'email' => 's'
    ];

    function __construct( $attributes = array() ){
        $this->setAttributes( $attributes );
    }

    //get user by username
    //throws exception
    public static function scopeByEmail( $email ){

        $user = null;

        try{
            $query = "select * from `account` WHERE email = ? limit 1";
            $argTypes = "s";
            $args = [];
            $args[] = &$argTypes;
            $args[] = &$email;
            $row = [];
            $dbHandler = new MySqlHandler();
            if( ! $dbHandler->dbConnected() ){
                throw new Exception('DB connection failed');
            }
            $select = $dbHandler->executePreparedQuery( $query, $args, $row );
            if( $select !== null && $select instanceof mysqli_stmt && $select->num_rows === 1 ){
                $select->fetch();
                $select->close();

                $user = new Account();
                $user->original = $row;
                $user->attributes = $row;
            }
            $dbHandler->close();
        }catch (Exception $e){
            unset($dbHandler);
            if( $e->getMessage == 'DB connection failed' ){
                throw new Exception( $e->getMessage() );
            }
        }

        return $user;
    }

}
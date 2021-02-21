<?php

namespace app\Models;

require_once __DIR__ . '/Model.php';

use Exception;
use mysqli_stmt;
use app\Utilities\MySqlHandler;

class Balance extends Model
{
    protected $table = 'balance';
    protected $key = 'balance_id';

    protected $fillable = [
        'balance_id',
        'account_id',
        'balance'
    ];

    protected $cols = [
        'balance_id' => 'i',
        'account_id' => 's',
        'balance' => 'd'
    ];

    function __construct( $attributes = array() ){
        $this->setAttributes( $attributes );
    }

    public function getBalance(){
        return $this->attributes['balance'];
    }

    public function getBalanceId(){
        return $this->attributes['balance_id'];
    }

    public function getAccountId(){
        return $this->attributes['account_id'];
    }

    public function setBalance( $balance ){
        $this->attributes['balance'] = $balance;
    }

    //get balance by account id
    //throws exception
    public static function scopeByAccountId( $id ){

        $balance = null;

        try{
            $query = "SELECT * FROM `balance` WHERE account_id = ? limit 1";
            $argTypes = "i";
            $args = [];
            $args[] = &$argTypes;
            $args[] = &$id;
            $row = [];
            $dbHandler = new MySqlHandler();
            if( ! $dbHandler->dbConnected() ){
                throw new Exception('DB connection failed');
            }
            $select = $dbHandler->executePreparedQuery( $query, $args, $row );
            if( $select !== null && $select instanceof mysqli_stmt && $select->num_rows > 0 ){
                $select->fetch();
                $select->close();
                $balance = new Balance();
                $balance->original = $row;
                $balance->attributes = $row;
            }
            $dbHandler->close();
        }catch (Exception $e){
            unset($dbHandler);
            if( $e->getMessage() == 'DB connection failed' ){
                throw new Exception( $e->getMessage() );
            }
        }

        return $balance;
    }
}
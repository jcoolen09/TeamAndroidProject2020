<?php

namespace app\Models;

require_once __DIR__ . '/Model.php';

use Exception;
use mysqli_stmt;
use app\Utilities\MySqlHandler;

class CheckIn extends Model
{
    protected $table = 'checkin';
    protected $key = 'checkin_id';

    protected $fillable = [
        'store_id',
        'account_id',
        'checkin_time'
    ];

    protected $cols = [
        'store_id' => 'i',
        'account_id' => 'i',
        'checkin_time' => 's'
    ];

    function __construct( $attributes = array() ){
        $this->setAttributes( $attributes );
    }

    //get checkin by account id
    //throws exception
    public static function scopeByAccountId( $id ){

        $checkins = [];

        try{
            $query = "SELECT * FROM `checkin` WHERE account_id = ? ORDER BY checkin_time DESC";
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
                while( $select->fetch() ){
                    $checkin = new CheckIn();
                    $values = $dbHandler->derefrence_array($row);
                    $checkin->original = $values;
                    $checkin->attributes = $values;
                    $checkins[] = $checkin;
                }

                $select->close();
            }
            $dbHandler->close();
        }catch (Exception $e){
            unset($dbHandler);
            if( $e->getMessage() == 'DB connection failed' ){
                throw new Exception( $e->getMessage() );
            }
        }

        return $checkins;
    }

    public static function scopeByAccountStoreTime( $account_id, $store_id ){

        $checkins = [];

        try{
            $query = "SELECT * FROM `checkin` WHERE account_id = ? AND store_id = ? AND TIMESTAMPDIFF(MINUTE,checkin_time,now()) < 30 LIMIT 1";
            $argTypes = "ii";
            $args = [];
            $args[] = &$argTypes;
            $args[] = &$account_id;
            $args[] = &$store_id;
            $row = [];
            $dbHandler = new MySqlHandler();
            if( ! $dbHandler->dbConnected() ){
                throw new Exception('DB connection failed');
            }
            $select = $dbHandler->executePreparedQuery( $query, $args, $row );
            if( $select !== null && $select instanceof mysqli_stmt && $select->num_rows > 0 ){
                while( $select->fetch() ){
                    $checkin = new CheckIn();
                    $values = $dbHandler->derefrence_array($row);
                    $checkin->original = $values;
                    $checkin->attributes = $values;
                    $checkins[] = $checkin;
                }

                $select->close();
            }
            $dbHandler->close();
        }catch (Exception $e){
            unset($dbHandler);
            if( $e->getMessage() == 'DB connection failed' ){
                throw new Exception( $e->getMessage() );
            }
        }

        return $checkins;
    }

    //get checkin by store id
    //throws exception
    public static function scopeByStoreId( $id ){

        $checkins = [];

        try{
            $query = "SELECT * FROM `checkin` WHERE store_id = ? ORDER BY checkin_time DESC";
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
                while( $select->fetch() ){
                    $checkin = new CheckIn();
                    $values = $dbHandler->derefrence_array($row);
                    $checkin->original = $values;
                    $checkin->attributes = $values;
                    $checkins[] = $checkin;
                }

                $select->close();
            }
            $dbHandler->close();
        }catch (Exception $e){
            unset($dbHandler);
            if( $e->getMessage() == 'DB connection failed' ){
                throw new Exception( $e->getMessage() );
            }
        }

        return $checkins;
    }
}
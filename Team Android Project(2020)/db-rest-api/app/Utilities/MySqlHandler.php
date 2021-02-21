<?php

namespace app\Utilities;

use mysqli;
use mysqli_stmt;

class MySqlHandler{

    public static $DUPLICATE_ERROR = 1062;

    //----DATABASE CONNECTION PROPERTIES----//
    private $db_host = 'remotemysql.com';
    private $db_port = 3306;
    private $db_user = 'o1KVJrHYj5';
    private $db_pass = 'qONICwBh77';
    private $db_schema = 'o1KVJrHYj5';

    //----DATABASE CONNECTION----//
    private $mysqli = null;
    private $dbConnected = false;

    //----PUBLIC ATTRIBUTES----//
    public $mysqliError = null;
    public $errorno = null;

    //----CONSTRUCTOR AND DESTRUCTOR----//

    function __construct(){
        $this->initiateDbConnection();
    }

    function __destruct(){
        //Close open mysqli connection
        if( $this->mysqli !== null ){
            $this->mysqli->close();
        }
    }

    //----PRIVATE FUNCTIONS----//

    //Retrives and sets appropriate error string
    private function setErrorString( $stmt = null ){
        if( $stmt !== null && $stmt instanceof mysqli_stmt ){
            $this->errorno = $stmt->errno;
            $this->mysqliError = "SQL Error: [" . $stmt->errno . "] "  . $stmt->error;
        }elseif( $this->mysqli !== null ){
            if( $this->mysqli->connect_errno ){
                $this->errorno = $this->mysqli->connect_errno;
                $this->mysqliError = "SQL Conn Error: [" . $this->mysqli->connect_errno . "] " . $this->mysqli->connect_error;
            }else{
                $this->errorno = $this->mysqli->errno;
                $this->mysqliError = "SQL Error: [" . $this->mysqli->errno . "] "  . $this->mysqli->error;
            }
        }else{
            $this->errorno = null;
            $this->mysqliError = "No current connections.";
        }
    }

    //----PUBLIC FUNCTIONS----//

    //----GETTER FUNCTIONS----//

    public function dbConnected(){
        return $this->dbConnected;
    }

    public function getLastInsertID(){
        $id = null;
        if( $this->mysqli !== null ){
            $id = $this->mysqli->insert_id;
        }

        return $id;
    }

    public function initiateDbConnection(){
        if( $this->mysqli == null ){
            $this->mysqli = new mysqli( $this->db_host, $this->db_user, $this->db_pass, $this->db_schema, $this->db_port );
            if( $this->mysqli ){
                $this->dbConnected = true;
            }else{
                $this->setErrorString();
            }
        }
    }

    //Used for SQL queries that such as UPDATE, DELETE, INSERT
    public function executeScalarQuery( &$query ){
        $result = null;
        if( $this->mysqli !== null ){
            $resultQuery = $this->mysqli->query( $query );
            if( !$resultQuery ){
                $this->setErrorString();
            }else{
                $result = $this->mysqli->affected_rows;
            }
        }

        return $result;
    }

    //Used for SQL queries that such as UPDATE, DELETE, INSERT
    public function executePreparedScalarQuery( &$query, &$arrArgs ){
        $result = null;
        if( $this->mysqli !== null ){
            $stmt = $this->mysqli->prepare( $query );
            if( $stmt ){
                call_user_func_array( array( $stmt, 'bind_param' ), $arrArgs );
                if( $stmt->execute() ){
                    $result = $stmt->affected_rows;
                }else{
                    $this->setErrorString( $stmt );
                }

                //Close prepared statement
                $stmt->close();
            }else{
                $this->setErrorString();
            }
        }

        return $result;
    }

    //Used for SQL queries such as SELECT
    public function executeQuery( &$query ){
        $result = null;
        if( $this->mysqli !== null ){
            $result = $this->mysqli->query( $query );
            if( !$result ){
                $this->setErrorString();
            }
        }

        return $result;
    }

    //Used for SQL queries such as SELECT
    public function &executePreparedQuery( &$query, &$arrArgs, &$arrResultCols ){
        $result = null;
        if( $this->mysqli !== null ){
            $stmt = $this->mysqli->prepare( $query );
            if( $stmt ){
                call_user_func_array( array( $stmt, 'bind_param' ) , $arrArgs );
                if( $stmt->execute() ){
                    //bind resulting fields to $arrResultCols
                    //result can be accessed by calling $stmt->fetch() and then $arrResultCols['colname']
                    $stmt->store_result();
                    $metadata = $stmt->result_metadata();
                    $fields = $metadata->fetch_fields();
                    $cols = array();
                    $arrResultCols = array();
                    $i = 0;
                    foreach( $fields as $field ){
                        $cols[ $i ] = &$arrResultCols[ $field->name ];
                        $i++;
                    }
                    call_user_func_array( array( $stmt, 'bind_result' ) , $cols );
                    $result = $stmt;

                    //closing of the statement will have to be done outside of class
                }else{
                    $this->setErrorString( $stmt );
                }
            }else{
                $this->setErrorString();
            }
        }

        return $result;
    }

    //Use specified schema
    public function use_db( $db ){
        $result = false;
        if( $this->mysqli == null ){
            $this->setErrorString();
        }else{
            $result = $this->mysqli->select_db( $db );
            if( !$result ){ $this->setErrorString(); }
        }

        return $result;
    }

    //Close db connection
    public function close(){
        $this->mysqli->close();
        $this->mysqli = null;
    }

    public function derefrence_array( $arr ){
        $tmp = array();
        foreach( $arr as $key => $value ){
            $tmp[$key] = $value;
        }

        return $tmp;
    }
}

?>

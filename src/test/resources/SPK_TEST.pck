CREATE OR REPLACE PACKAGE SPK_TEST IS

  -- Author  : XUE
  -- Created : 2018/2/1 14:11:00
  -- Purpose : spk_test

  -- Public type declarations
  TYPE CURSOR_TYPE IS REF CURSOR;

  PROCEDURE SP_XXX_LIST(ID      IN NUMBER,
                        NAME    IN VARCHAR2,
                        RETCODE OUT NUMBER,
                        RETMSG  OUT VARCHAR2,
                        PO_LIST OUT SPK_TEST.CURSOR_TYPE);
PROCEDURE SP_XXX(ID      IN NUMBER,
                        NAME    IN VARCHAR2,
                        RETCODE OUT NUMBER,
                        RETMSG  OUT VARCHAR2);                        

END SPK_TEST;
/
CREATE OR REPLACE PACKAGE BODY SPK_TEST IS

  PROCEDURE SP_XXX_LIST(ID      IN NUMBER,
                        NAME    IN VARCHAR2,
                        RETCODE OUT NUMBER,
                        RETMSG  OUT VARCHAR2,
                        PO_LIST OUT SPK_TEST.CURSOR_TYPE) IS
  
  BEGIN
  
    RETCODE := ID + ID;
    RETMSG  := '你的名字是' || NAME;
    OPEN  po_list FOR SELECT * FROM aa08;
  END SP_XXX_LIST;

PROCEDURE SP_XXX(ID      IN NUMBER,
                        NAME    IN VARCHAR2,
                        RETCODE OUT NUMBER,
                        RETMSG  OUT VARCHAR2) IS
  
  BEGIN
  
    RETCODE := ID + ID;
    RETMSG  := '你的名字是' || NAME;
  END SP_XXX;
END SPK_TEST;
/

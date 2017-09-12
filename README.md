# EditTextTransFormAndroidPC
transForm Entire space and half space from PC to Android

**用法**
``` java
  private EditTextTransFormAndroidPC editTextDemo = null;
  
  //...
  
  String string = null;
  if(null != mEntityBean) {
     // mEntityBean是从服务器下载回来的;string就是未处理的数据
     string = this.mEntityBean.getString("nbyj", "");
  }
  editTextDemo = new EditTextTransFormAndroidPC(mContext);
  // 转换成适用于Android的并设置到文本框
  editTextDemo.setAndroidTextFromPC(setAndroidTextFromPC);
  string = null;
  
  //...
  
  // 保存数据
  if(editTextDemo != null {
    mEntityBean.set("nbyj", editTextDemo.getAndroidTextToPC());
    // save(mEntityBean)...    
 }
  
  //...
```

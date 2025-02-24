package cn.gsq.host.common;

import cn.hutool.json.JSONUtil;

/**
* Project : host
* Class : cn.gsq.host.common.BaseModel
* @author : gsq
* @date : 2024-09-09 16:35
* @note : It's not technology, it's art !
**/
public abstract class BaseModel {

    @Override
    public String toString() {
        return JSONUtil.toJsonStr(this);
    }

}

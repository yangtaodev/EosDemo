/*
 * Copyright (c) 2017 Mithril coin.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.mithrilcoin.eoscommander.data.remote.model.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.mithrilcoin.eoscommander.crypto.util.HexUtils;
import io.mithrilcoin.eoscommander.data.remote.model.types.EosType;
import io.mithrilcoin.eoscommander.data.remote.model.types.TypeAccountName;
import io.mithrilcoin.eoscommander.data.remote.model.types.TypePermissionLevel;
import io.mithrilcoin.eoscommander.data.remote.model.types.TypeActionName;

/**
 * Created by swapnibble on 2017-09-11.
 */

public class Action implements EosType.Packer {
    private TypeAccountName account;

    private TypeActionName name;

    @Expose
    private List<TypePermissionLevel> authorization = null;

    @Expose
    private String data;

    public Action(String account, String name, TypePermissionLevel authorization, String data){
        this.account = new TypeAccountName(account);
        this.name = new TypeActionName(name);
        this.authorization = new ArrayList<>();
        if ( null != authorization ) {
            this.authorization.add(authorization);
        }

        if ( null != data ) {
            this.data = data;
        }
    }

    public Action(String account, String name) {
        this (account, name, null, null);
    }

    public Action(){
        this ( null, null, null, null);
    }

    public String getAccount() {
        return account.toString();
    }

    public void setAccount(String account) {
        this.account = new TypeAccountName(account);
    }

    public String getName() {
        return name.toString();
    }

    public void setName(String name) {
        this.name = new TypeActionName(name) ;
    }

    public List<TypePermissionLevel> getAuthorization() {
        return authorization;
    }

    public void setAuthorization(List<TypePermissionLevel> authorization) {
        this.authorization = authorization;
    }

    public void setAuthorization(TypePermissionLevel[] authorization) {
        this.authorization.addAll( Arrays.asList( authorization) );
    }

    public void setAuthorization(String[] accountWithPermLevel) {
        if ( null == accountWithPermLevel){
            return;
        }

        for ( String permissionStr : accountWithPermLevel ) {
            String[] split = permissionStr.split("@", 2);
            authorization.add( new TypePermissionLevel(split[0], split[1]) );
        }
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public void pack(EosType.Writer writer) {
        account.pack(writer);
        name.pack(writer);

        writer.putCollection( authorization );

        if ( null != data ) {
            byte[] dataAsBytes = HexUtils.toBytes( data);
            writer.putVariableUInt(dataAsBytes.length);
            writer.putBytes( dataAsBytes );
        }
        else {
            writer.putVariableUInt(0);
        }
    }

    public static class GsonTypeAdapterFactory extends EoscGsonTypeAdapterFactory<Action> {
        public GsonTypeAdapterFactory(){
            super(Action.class);
        }

        @Override
        protected void beforeWrite(Action source, JsonElement toSerialize) {
            JsonObject jsonObject = toSerialize.getAsJsonObject();
            jsonObject.addProperty("account", source.getAccount());
            jsonObject.addProperty("name", source.getName());
        }

        @Override
        protected void afterRead(Action source, JsonElement deserialized) {
            JsonObject jsonObject = deserialized.getAsJsonObject();
            source.setAccount( jsonObject.get("account").getAsString() );
            source.setName( jsonObject.get("name").getAsString() );
        }
    }
}
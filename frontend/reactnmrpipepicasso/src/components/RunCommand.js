import React, {useContext} from "react";
import http from "../http-common";
import {ContextInfo} from "../contexts/ContextInfo";

export default function RunCommand(){

    const value = useContext(ContextInfo);
    console.log('Sono in Runcommand');
    console.log(value.dirtmp);
    const runcommand = () => {
        http.get('/run', {params:{sessid: value.dirtmp}})

    }
    return (
        <div>
            <button onClick={() => runcommand()}>
                Cliccami
            </button>
        </div>
    );
};
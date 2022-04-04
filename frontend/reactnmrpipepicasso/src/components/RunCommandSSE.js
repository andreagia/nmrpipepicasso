import React, {useContext, useEffect, useState} from "react";
import http from "../http-common";
import {ContextInfo} from "../contexts/ContextInfo";

import ImageSSE from "./ImageSSE";

export default function RunCommandSSE(){

    const value = useContext(ContextInfo);
    const [listening, setListening] = useState(false);
    console.log('Sono in RuncommandSSE');
    console.log(value.dirtmp);
    useEffect(() => {
        if (listening){
            console.log("PIPPO ", value.dirtmp);
            http.get('/run', {params:{sessid: value.dirtmp, run: 'run'}});

        }
    })
    return (
        <div>
            <button onClick={() => setListening(true)}>
                CliccamiSSE
            </button>
            {listening ? <button>Logout</button> : <button>Login</button>}
            { listening ? <ImageSSE dirtmp={value.dirtmp} /> : '' }
        </div>
    );
};
import React, { Component } from "react";
import UploadService from "../services/upload-files.service";
import {ContextInfo} from "../contexts/ContextInfo";

//https://www.bezkoder.com/react-multiple-files-upload/

export default class UploadFiles extends Component {
    constructor(props) {
        super(props);
        this.selectFiles = this.selectFiles.bind(this);
        this.upload = this.upload.bind(this);
        this.uploadFiles = this.uploadFiles.bind(this);
        this.uploadFilecsv = this.uploadFilecsv.bind(this);
        this.handleChangetmpdir = this.handleChangetmpdir.bind(this);
        this.handleSubmittmpdir = this.handleSubmittmpdir.bind(this);
        this.selectFilecsv = this.selectFilecsv.bind(this);

        this.state = {
            selectedFiles: undefined,
            progressInfos: [],
            message: [],
            fileInfos: [],
            timerview: false,
            selectedFilecsv: undefined
        };
    }

    static contextType = ContextInfo;

    componentDidMount() {
        console.log('ComponentDidmount');
        console.log(this.context);
        console.log(this.context.dirtmp);
     //   const {dirtmp} = this.context;
        UploadService.getFiles(this.context.dirtmp).then((response) => {
            this.setState({
                fileInfos: response.data
            });
        });
    }

    selectFilecsv(event) {
        console.log(event);
        this.setState({
            progressInfos: [],
            selectedFilecsv: event.target.files,
        });
    }

    selectFiles(event) {
        this.setState({
            progressInfos: [],
            selectedFiles: event.target.files,
        });
    }

    uploadcsv(idx, file) {


        UploadService.uploadcsv(file, this.context.dirtmp)
            .then((response) => {
                this.setState((prev) => {
                    let nextMessage = [...prev.message, "Uploaded the file successfully: " + file.name];
                    return {
                        message: nextMessage
                    };
                });

                return UploadService.getFiles(this.context.dirtmp);
            })
            .then((files) => {
                this.setState({
                    fileInfos: files.data,
                    timerview: true
                });
            })
            .catch(() => {

                this.setState((prev) => {
                    let nextMessage = [...prev.message, "Could not upload the file: " + file.name];
                    return {
                        message: nextMessage
                    };
                });
            });
    }
    upload(idx, file) {
        let _progressInfos = [...this.state.progressInfos];

        UploadService.upload(file, this.context.dirtmp,(event) => {
            _progressInfos[idx].percentage = Math.round((100 * event.loaded) / event.total);
            this.setState({
                _progressInfos,
            });
        })
            .then((response) => {
                this.setState((prev) => {
                    let nextMessage = [...prev.message, "Uploaded the file successfully: " + file.name];
                    return {
                        message: nextMessage
                    };
                });

                return UploadService.getFiles(this.context.dirtmp);
            })
            .then((files) => {
                this.setState({
                    fileInfos: files.data,
                    timerview: true
                });
            })
            .catch(() => {
                _progressInfos[idx].percentage = 0;
                this.setState((prev) => {
                    let nextMessage = [...prev.message, "Could not upload the file: " + file.name];
                    return {
                        progressInfos: _progressInfos,
                        message: nextMessage
                    };
                });
            });
    }

    uploadFilecsv() {
        const selectedFile = this.state.selectedFilecsv;
        let _progressInfos = [];
            _progressInfos.push({ percentage: 0, fileName: selectedFile[0].name });
        this.setState(
            {
                progressInfos: _progressInfos,
                message: [],
            },
            () => {

                    this.uploadcsv(200, selectedFile[0]);

            }
        );
    }

    uploadFiles() {
        const selectedFiles = this.state.selectedFiles;

        let _progressInfos = [];

        for (let i = 0; i < selectedFiles.length; i++) {
            _progressInfos.push({ percentage: 0, fileName: selectedFiles[i].name });
        }

        this.setState(
            {
                progressInfos: _progressInfos,
                message: [],
            },
            () => {
                for (let i = 0; i < selectedFiles.length; i++) {
                    this.upload(i, selectedFiles[i]);
                }
            }
        );
    }

    handleChangetmpdir(e) {
        this.context.setdirtmp(e.target.value)
        console.log('Sono qui===================================================================')
        console.log(this.context.dirtmp);
        UploadService.getFiles(this.context.dirtmp).then((response) => {
            this.setState({
                fileInfos: response.data
            });
        });
    }

    handleSubmittmpdir(e) {
        e.preventDefault();
        this.context.setdirtmp(e.target.value)

        console.log(this.context.dirtmp);
         UploadService.getFiles(this.context.dirtmp).then((response) => {
             this.setState({
                 fileInfos: response.data
             });
         });

    }
    render() {
        const { selectedFiles, progressInfos, message, fileInfos, timerview, selectedFilecsv } = this.state;
      //  this.setState({tempdir: this.context.dirtmp.getSession()});
        console.log('Sono in Render');
        console.log(this.context)
        // if(this.context.dirtmp === '') {
        //     this.context.setdirtmp(Math.random().toString(36).substr(2, 10))
        // }
        if (timerview) {
            setTimeout(() => {
                this.setState({timerview: false})
            }, 5000)
        }

        console.log(this.context)
        return (
            <div>
                <form onSubmit={this.handleSubmittmpdir}>
                    <label>
                        Nome:
                        <input type="text" defaultValue={ this.context.dirtmp} onChange={this.handleChangetmpdir} />
                    </label>
                    <input type="submit" value="Submit" />
                </form>
                {progressInfos && timerview &&
                    progressInfos.map((progressInfo, index) => (
                        <div className="mb-2" key={index}>
                            <span>{progressInfo.fileName}</span>
                            <div className="progress">
                                <div
                                    className="progress-bar progress-bar-info"
                                    role="progressbar"
                                    aria-valuenow={progressInfo.percentage}
                                    aria-valuemin="0"
                                    aria-valuemax="100"
                                    style={{ width: progressInfo.percentage + "%" }}
                                >
                                    {progressInfo.percentage}%
                                </div>
                            </div>
                        </div>
                    ))}

                <div className="row my-3">

                    <div className="col-8">
                        <label className="btn btn-default p-0">
                            <input type="file" multiple onChange={this.selectFiles} />
                        </label>
                    </div>

                    <div className="col-4">
                        <button
                            className="btn btn-success btn-sm"
                            disabled={!selectedFiles}
                            onClick={this.uploadFiles}
                        >
                            Upload
                        </button>
                    </div>
                </div>
                <div className="row my-3">
                    CSV
                    <div className="col-8">
                        <label className="btn btn-default p-0">
                            <input type="file"  onChange={this.selectFilecsv} />
                        </label>
                    </div>

                    <div className="col-4">
                        <button
                            className="btn btn-success btn-sm"
                            disabled={!selectedFilecsv}
                            onClick={this.uploadFilecsv}
                        >
                            Upload
                        </button>
                    </div>
                </div>

                {message.length > 0 && timerview && (
                    <div className="alert alert-secondary" role="alert">
                        <ul>
                            {message.map((item, i) => {
                                return <li key={i}>{item}</li>;
                            })}
                        </ul>
                    </div>
                )}

                <div className="card">
                    <div className="card-header">List of Files</div>
                    <ul className="list-group list-group-flush">
                        {fileInfos &&
                            fileInfos.map((file, index) => (
                                <li className="list-group-item" key={index}>
                                    <a href={file.url}>{file.name}</a>
                                </li>
                            ))}
                    </ul>
                </div>
            </div>
        );
    }
}
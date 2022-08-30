import http from "../http-common";

class UploadFilesService {
    upload(file, tmpdir,  onUploadProgress) {
        let formData = new FormData();

        formData.append("file", file);
        formData.append('sessid', tmpdir);

        return http.post("/upload", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
            onUploadProgress,
        });
    }
    uploadcsv(file, tmpdir,  onUploadProgress) {
        let formData = new FormData();

        formData.append("file", file);
        formData.append('sessid', tmpdir);

        return http.post("/uploadcsv", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
            },
            onUploadProgress,
        });
    }

    getFiles(tmpdir) {
        return http.get("/files", {params:{sessid: tmpdir}});
    }

}

export default new UploadFilesService();
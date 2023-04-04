import Api from "./Api";
import { JsonData } from "./window/RestfulFetch";

type ApiError = {
  id: number;
  message: string;
};

type ApiStatus = {
  states: boolean[];
  errors: ApiError[];
};

class ManagedApi {
  apiStatus: ApiStatus;

  api: Api;

  constructor(apiStatus: ApiStatus) {
    this.apiStatus = apiStatus;
    this.api = new Api("/api/");
  }

  private assignLoading(value: boolean) {
    if (value) {
      this.apiStatus.states.push(true);
    } else {
      this.apiStatus.states.pop();
    }
  }

  async around<T>(promise: Promise<T>): Promise<T> {
    this.assignLoading(true);
    try {
      try {
        return await promise;
      } catch (error) {
        if (error instanceof Error) {
          this.apiStatus.errors.push({
            message: error.message,
            id: Date.now(),
          });
        }
        throw error;
      }
    } finally {
      this.assignLoading(false);
    }
  }

  restGet(url: string) {
    return this.around(this.api.restGet(url));
  }

  restPost(url: string, data: JsonData) {
    return this.around(this.api.restPost(url, data));
  }

  restPatch(url: string, data: JsonData) {
    return this.around(this.api.restPatch(url, data));
  }

  restPostMultiplePartForm(url: string, data: JsonData) {
    return this.around(this.api.restPostMultiplePartForm(url, data));
  }

  restPatchMultiplePartForm(url: string, data: JsonData) {
    return this.around(this.api.restPatchMultiplePartForm(url, data));
  }

  restPostWithHtmlResponse(url: string, data: JsonData) {
    return this.around(this.api.restPostWithHtmlResponse(url, data));
  }
}

export default ManagedApi;
export type { ApiStatus, ApiError };
